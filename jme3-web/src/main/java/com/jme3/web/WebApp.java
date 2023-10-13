
package com.jme3.web;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.jme.jmetx.JmeTxLoader;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.asset.plugins.UrlLocator;
import com.jme3.audio.AudioListenerState;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.environment.EnvironmentProbeControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.light.LightProbe;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.plugins.json.Json;
import com.jme3.plugins.json.JsonArray;
import com.jme3.plugins.json.JsonElement;
import com.jme3.plugins.json.JsonObject;
import com.jme3.plugins.json.JsonPrimitive;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.Caps;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.post.filters.ContrastAdjustmentFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.LightControl;
import com.jme3.shadow.AbstractShadowFilter;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.util.BufferAllocatorFactory;
import com.jme3.util.SkyFactory;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;
import com.jme3.web.context.HeapAllocator;
import com.jme3.web.context.JmeWebSystem;
import com.jme3.web.context.NativeUtils;
import com.jme3.web.filesystem.PrefetchedHttpZipLocator;
import com.jme3.web.filesystem.StbImageLoader;

/**
 * TestPBRSimple
 */
public class WebApp extends SimpleApplication implements ActionListener {
    private static final Logger logger = Logger.getLogger(WebApp.class.getName());

    public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
            InstantiationException, NoSuchFieldException {

        String implementation = BufferAllocatorFactory.PROPERTY_BUFFER_ALLOCATOR_IMPLEMENTATION;
        System.setProperty(implementation, HeapAllocator.class.getName());

        JmeSystem.setSystemDelegate(new JmeWebSystem());

        AppSettings settings = new AppSettings(true);
        settings.setEmulateMouse(true);
        settings.setWidth(1024);
        settings.setResizable(true);
        settings.setHeight(768);
        settings.setGammaCorrection(true);
        settings.setSamples(4);
        settings.setFullscreen(false);
        settings.setTitle("Model Viewer");
        settings.setGraphicsDebug(false);

        AppState appStates[] = { new BulletAppState(), new FlyCamAppState(), new AudioListenerState(), new ConstantVerifierState() };

        SimpleApplication app = null;

        app = new WebApp(appStates);

        app.setSettings(settings);
        app.start();

    }

    private ChaseCamera chaseCam;
    private float xRot = 0;
    private int loadingEnvFrames;
    private float rotationSpeed = 0.14f;
    private boolean autoRotate = true;
    private FilterPostProcessor fpp;
    private Spatial bound;

    private float steps = 8;
    private float increment = 1f / steps;
    private float progress = 0;

    public WebApp(AppState... initialStates) {
        super(initialStates);
    }

    private Spatial findBound(Spatial s) {
        if (!(s instanceof Node)) return null;
        Node parent = (Node) s;

        Spatial n[] = new Spatial[1];
        parent.depthFirstTraversal(sx -> {
            if (n[0] != null) return;
            System.out.println("USER DATA " + sx.getUserDataKeys());
            Object bound = sx.getUserData("bound");
            if (bound != null) {
                n[0] = sx;
            }
        });
        return n[0];
    }

    @Override
    public void simpleInitApp() {
        JmeTxLoader.initFormats(renderer);
        JmeTxLoader.registerLoader(assetManager, StbImageLoader.class, "png", "jpg", "bmp", "jpeg");

        // BulletAppState bulletAppState =
        // stateManager.getState(BulletAppState.class);

        inputManager.addMapping("MouseClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "MouseClick");

        chaseCam = new ChaseCamera(cam, bound = new Node(), inputManager);

        chaseCam.setDragToRotate(true);
        chaseCam.setMinVerticalRotation(-FastMath.HALF_PI);

        chaseCam.setSmoothMotion(true);
        chaseCam.setRotationSensitivity(10);
        chaseCam.setZoomSensitivity(5);
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);

        fpp = new FilterPostProcessor(assetManager);
        int numSamples = context.getSettings().getSamples();
        if (numSamples > 0) {
            renderManager.setAlphaToCoverage(true);
            fpp.setNumSamples(numSamples);
        }
        renderManager.getRenderer().setDefaultAnisotropicFilter(4);
        rootNode.setShadowMode(ShadowMode.CastAndReceive);

        fpp.addFilter(new ToneMapFilter(Vector3f.UNIT_XYZ.mult(1)));
        fpp.addFilter(new ContrastAdjustmentFilter(1f / 2.2f));
        fpp.addFilter(new SSAOFilter(3.1f, 1.8f, 0.2f, 0.2f));
        fpp.addFilter(new FXAAFilter());
        viewPort.addProcessor(fpp);

    }

    Spatial sky;
    EnvironmentProbeControl envProbe;
    String textBakingEnvironment;
    List<String> assetsLocator = new ArrayList<>();
 

    private void processMessage(JsonObject messageData) {
        JsonPrimitive type = messageData.getAsJsonPrimitive("type");
        if (type == null) return;
        System.out.println(type.getAsString());

        switch (type.getAsString()) {
            case "setEnvironment": {
                NativeUtils.setProgress(0, "");
                loadingEnvFrames = 0;
                this.progress = 0;

                String textLoadingEnvironment = messageData.getAsJsonPrimitive("textLoadingEnvironment") != null
                        ? messageData.getAsJsonPrimitive("textLoadingEnvironment").getAsString()
                        : "Loading environment";

                textBakingEnvironment = messageData.getAsJsonPrimitive("textBakingEnvironment") != null ? messageData.getAsJsonPrimitive("textBakingEnvironment").getAsString()
                        : "Preparing environment";
                NativeUtils.increaseProgress(increment, textLoadingEnvironment);

                JsonPrimitive envMap = messageData.getAsJsonPrimitive("environment");
                JsonPrimitive envSize = messageData.getAsJsonPrimitive("envSize");
                JsonPrimitive showSky = messageData.getAsJsonPrimitive("showSky");
                JsonPrimitive whitePoint = messageData.getAsJsonPrimitive("whitePoint");

                if (sky != null) {
                    sky.removeFromParent();
                    sky = null;
                }

                if (envProbe != null) {
                    rootNode.removeControl(envProbe);
                    envProbe = null;
                }

                if (envMap == null) return;

                ToneMapFilter toneMapFilter = fpp.getFilter(ToneMapFilter.class);
                if (toneMapFilter != null) toneMapFilter.setWhitePoint(Vector3f.UNIT_XYZ.mult(whitePoint == null ? 1f : whitePoint.getAsFloat()));

                sky = SkyFactory.createSky(assetManager, envMap.getAsString(), SkyFactory.EnvMapType.EquirectMap);
                if (showSky != null && showSky.getAsBoolean()) {
                    rootNode.attachChild(sky);
                }

                // Create baker control
                envProbe = new EnvironmentProbeControl(assetManager, envSize == null ? 64 : envSize.getAsInt());
                envProbe.setEnvironment(sky);
                rootNode.addControl(envProbe);

                // Tag the sky, only the tagged spatials will be rendered in the
                // env map
                EnvironmentProbeControl.tag(sky);

                NativeUtils.increaseProgress((increment), textLoadingEnvironment);
                return;
            }
            case "setModel": {
                NativeUtils.setProgress(0, "");
                inputManager.setCursorVisible(true);
                this.progress = 0;
                String textLoadingGeometry = messageData.getAsJsonPrimitive("textLoadingGeometry") != null ? messageData.getAsJsonPrimitive("textLoadingGeometry").getAsString()
                        : "Loading model";

                NativeUtils.increaseProgress((increment), textLoadingGeometry);

                for (Spatial child : rootNode.getChildren()) {
                    if (child != sky) {
                        child.removeFromParent();
                    }
                }

                for (Filter f : fpp.getFilterList()) {
                    if (f instanceof AbstractShadowFilter) {
                        fpp.removeFilter(f);
                    }
                }

                for (int i = 0; i < rootNode.getNumControls(); i++) {
                    Control c = rootNode.getControl(i);
                    if (c instanceof LightControl) {
                        rootNode.removeControl(c);
                        i--;

                    }
                }

                for (int i = 0; i < rootNode.getLocalLightList().size(); i++) {
                    Light l = rootNode.getLocalLightList().get(i);
                    if (!(l instanceof LightProbe)) {
                        rootNode.removeLight(l);
                        i--;
                    }

                }

                if (bound != null) {
                    bound.removeControl(chaseCam);
                    bound.removeFromParent();
                }

                for (String l : assetsLocator) {
                    if (l.endsWith(".zip")) {
                        assetManager.unregisterLocator(l, HttpZipLocator.class);
                    } else {
                        assetManager.unregisterLocator(l, UrlLocator.class);
                    }
                }

                JsonPrimitive shadowIntensity = messageData.getAsJsonPrimitive("shadowIntensity");
                JsonPrimitive modelPath = messageData.getAsJsonPrimitive("model");
                JsonPrimitive rotationSpeed = messageData.getAsJsonPrimitive("rotationSpeed");
                JsonPrimitive shadowMapSize = messageData.getAsJsonPrimitive("shadowMapSize");
                JsonPrimitive shadowMapSamples = messageData.getAsJsonPrimitive("shadowMapSamples");
                JsonPrimitive scale = messageData.getAsJsonPrimitive("modelScale");
                JsonArray cameraCenter = messageData.getAsJsonArray("cameraCenter");
                JsonPrimitive cameraDistance = messageData.getAsJsonPrimitive("cameraDistance");
                JsonPrimitive material = messageData.getAsJsonPrimitive("material");
                JsonArray assetRoots = messageData.getAsJsonArray("assetRoots");
                JsonPrimitive generateTangents = messageData.getAsJsonPrimitive("generateTangents");
                JsonPrimitive zoomable = messageData.getAsJsonPrimitive("zoomable");
                JsonObject extraAssetsRoots = messageData.getAsJsonObject("extraAssetsRoots");

                if (assetRoots != null) {
                    for (int i = 0; i < assetRoots.size(); i++) {
                        String locator = assetRoots.get(i).getAsString();
                        assetsLocator.add(locator);
                    }
                }

                if (extraAssetsRoots != null) {
                    Consumer<String> addToAssetsLocator = (name) -> {
                        System.out.println(name);
                        JsonElement el = extraAssetsRoots.get(name);
                        System.out.println(el);
                        if (el == null) return;
                        el.getAsJsonArray().forEach((e) -> {
                            String locator = e.getAsString();
                            System.out.println(locator);
                            assetsLocator.add(locator);
                        });

                    };

                    if (renderer.getCaps().contains(Caps.TextureCompressionS3TC)) {
                        addToAssetsLocator.accept("s3tc");
                    } else if (renderer.getCaps().contains(Caps.TextureCompressionETC2)) {
                        addToAssetsLocator.accept("etc2");
                    } else if (renderer.getCaps().contains(Caps.TextureCompressionETC1)) {
                        addToAssetsLocator.accept("etc1");
                    } else {
                        addToAssetsLocator.accept("raw");
                    }
                }
        
                for (String locator : assetsLocator) {
                    System.out.println("Load "+locator);
                    if (locator.endsWith(".zip")) {
                        assetManager.registerLocator(locator, PrefetchedHttpZipLocator.class);
                    } else {
                        assetManager.registerLocator(locator, UrlLocator.class);
                    }
                }

                if (modelPath == null) return;

                this.rotationSpeed = rotationSpeed != null ? rotationSpeed.getAsFloat() : 0.14f;

                Spatial model = assetManager.loadModel(modelPath.getAsString());
                if (scale != null) {
                    model.setLocalScale(scale.getAsFloat());
                }
                if (material != null) {
                    model.setMaterial(assetManager.loadMaterial(material.getAsString()));
                }

                if (generateTangents != null && generateTangents.getAsBoolean()) {
                    MikktspaceTangentGenerator.generate(model);
                }
                rootNode.attachChild(model);
                stateManager.getState(BulletAppState.class).getPhysicsSpace().add(model);

                bound = findBound(model);
                if (bound == null) {
                    System.out.println("Bound not found. Create");
                    bound = new Node("bound");
                    bound.setUserData("bound", 1);
                    rootNode.attachChild(bound);

                    BoundingBox bbox = (BoundingBox) model.getWorldBound();
                    Vector3f boundCenter = new Vector3f(0, 0, 0);
                    Vector3f boundSize = new Vector3f(1, 1, 1);

                    if (cameraCenter != null) {
                        System.out.println(cameraCenter);
                        boundCenter = new Vector3f(cameraCenter.get(0).getAsNumber().floatValue(), cameraCenter.get(1).getAsNumber().floatValue(),
                                cameraCenter.get(2).getAsNumber().floatValue());
                    } else {
                        boundCenter.set(bbox.getCenter());
                    }

                    if (cameraDistance != null) {
                        boundSize = new Vector3f(cameraDistance.getAsFloat(), cameraDistance.getAsFloat(), cameraDistance.getAsFloat());
                    } else {
                        boundSize.set(bbox.getXExtent(), bbox.getYExtent(), bbox.getZExtent());
                    }

                    bound.setLocalTranslation(boundCenter);
                    bound.setLocalScale(boundSize);
                } else {
                    bound.removeFromParent();
                    rootNode.attachChild(bound);
                }

                Vector3f ws = bound.getWorldScale();
                float distance = ws.x;
                if (ws.y > distance) distance = ws.y;
                if (ws.z > distance) distance = ws.z;

                System.out.println("Camera center " + bound.getWorldTranslation());
                System.out.println("Camera distance " + distance);

                chaseCam.setMaxDistance(distance * 6);
                chaseCam.setMinDistance(distance);
                chaseCam.setDefaultDistance(distance * 2.5f);
                if (zoomable != null && !zoomable.getAsBoolean()) {
                    chaseCam.setZoomSensitivity(0);
                } else {
                    chaseCam.setZoomSensitivity(2f);
                }
                bound.addControl(chaseCam);
                autoRotate = true;
                xRot = 0;

                model.depthFirstTraversal(sx -> {
                    LightList ll = sx.getLocalLightList();
                    ArrayList<Light> lights = new ArrayList<>();
                    for (int i = 0; i < ll.size(); i++) {
                        lights.add(ll.get(i));
                    }

                    for (Light l : lights) {
                        sx.removeLight(l);
                        rootNode.addLight(l);
                        LightControl lc = new LightControl(l);
                        sx.addControl(lc);

                        if (shadowIntensity != null && shadowIntensity.getAsFloat() > 0) {
                            if (l instanceof DirectionalLight) {
                                DirectionalLight dl = (DirectionalLight) l;
                                DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, shadowMapSize != null ? shadowMapSize.getAsInt() : 512,
                                        shadowMapSamples != null ? shadowMapSamples.getAsInt() : 3);
                                dlsf.setLight(dl);

                                dlsf.setShadowIntensity(shadowIntensity.getAsFloat());
                                dlsf.setShadowCompareMode(CompareMode.Hardware);
                                dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
                                fpp.addFilter(dlsf);
                            }
                        }
                    }
                });

                return;
            }
        }

    }

    @Override
    public void simpleUpdate(float tpf) {
        try {
            String messages[] = NativeUtils.getPostedMessages();

            for (String m : messages) {
                System.out.println(m);
                JsonObject messageData = Json.create().parse(new ByteArrayInputStream(m.getBytes()));
                processMessage(messageData);
            }

            if (loadingEnvFrames < steps) {
                if (loadingEnvFrames >= steps - 1) {
                    NativeUtils.setProgress(1, "");
                } else {
                    NativeUtils.increaseProgress((increment), textBakingEnvironment);
                }
                loadingEnvFrames++;
            }

            if (autoRotate) {
                xRot -= tpf * rotationSpeed;
                chaseCam.setDefaultHorizontalRotation(xRot);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("MouseClick") && isPressed) {
            autoRotate = false;
        }
    }
}