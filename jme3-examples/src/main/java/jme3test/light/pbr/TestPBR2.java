
package jme3test.light.pbr;

import java.util.ArrayList;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.bounding.BoundingBox;
import com.jme3.environment.EnvironmentProbeControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.BloomFilter.GlowMode;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.post.filters.ContrastAdjustmentFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.util.SkyFactory;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;

/**
 * TestPBRSimple
 */
public class TestPBR2 extends SimpleApplication implements ActionListener {
    private boolean REALTIME_BAKING = false;



    public static void main(String[] args) {
        new TestPBR2().start();
    }
 

    float steps = 8;
    float increment = 1f / steps;
    float progress = 0;
    ChaseCamera chaseCam;
    float xRot = 0;
    int frames;
    float rotationSpeed = 0.14f;
    boolean autoRotate = true;
    int envSize = 128;
    String environment;
    String object;
    float whitePoint;
    boolean showSky = true;
    float shadowIntensity = 0;

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

        inputManager.addMapping("MouseClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "MouseClick");

        environment = "cantinette/studio_country_hall_1k.hdr";
        object = "cantinette/frassino-grezzo.gltf";
        whitePoint = 1f;
        envSize = 128;
        showSky = false;
        shadowIntensity = 0.3f;

        // NativeUtils.setProgress((this.progress += increment), "Loading geometry");

        Spatial model = assetManager.loadModel(object);
        rootNode.attachChild(model);

        Spatial bound = findBound(model);
        if (bound == null) {
            System.out.println("Bound not found. Create");
            bound = new Node("bound");
            bound.setUserData("bound", 1);
            rootNode.attachChild(bound);

            BoundingBox bbox = (BoundingBox) model.getWorldBound();
            Vector3f sceneCenter = bbox.getCenter();
            bound.setLocalTranslation(sceneCenter);
            bound.setLocalScale(bbox.getXExtent(), bbox.getYExtent(), bbox.getZExtent());
        }

        Vector3f ws = bound.getWorldScale();
        float distance = ws.x;
        if (ws.y > distance) distance = ws.y;
        if (ws.z > distance) distance = ws.z;

        // NativeUtils.setProgress((this.progress += increment), "Loading camera");
        chaseCam = new ChaseCamera(cam, bound, inputManager);

        chaseCam.setDragToRotate(true);
        chaseCam.setMinVerticalRotation(-FastMath.HALF_PI);
        chaseCam.setMaxDistance(distance * 6);
        chaseCam.setMinDistance(distance);
        chaseCam.setDefaultDistance(distance * 2.5f);

        chaseCam.setSmoothMotion(true);
        chaseCam.setRotationSensitivity(10);
        chaseCam.setZoomSensitivity(5);
        flyCam.setEnabled(false);

        // NativeUtils.setProgress((this.progress += increment), "Loading skybox");
        Spatial sky = SkyFactory.createSky(assetManager, environment, SkyFactory.EnvMapType.EquirectMap);
        if(showSky)rootNode.attachChild(sky);

        // Create baker control
        EnvironmentProbeControl envProbe = new EnvironmentProbeControl(assetManager, envSize);
        envProbe.setEnvironment(sky);
        rootNode.addControl(envProbe);

        // Tag the sky, only the tagged spatials will be rendered in the env map
        EnvironmentProbeControl.tag(sky);

        // NativeUtils.setProgress((this.progress += increment), "Loading filters");
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        int numSamples = context.getSettings().getSamples();
        if (numSamples > 0) {
            fpp.setNumSamples(numSamples);
        }


        rootNode.setShadowMode(ShadowMode.CastAndReceive);

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

                if (shadowIntensity > 0) {
                    if (l instanceof DirectionalLight) {
                        DirectionalLight dl = (DirectionalLight) l;
                        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 512, 3);
                        dlsf.setLight(dl);

                        dlsf.setShadowIntensity(shadowIntensity);
                        dlsf.setShadowCompareMode(CompareMode.Hardware);
                        dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
                        fpp.addFilter(dlsf);
                    }
                }
            }
        });

                fpp.addFilter(new ToneMapFilter(Vector3f.UNIT_XYZ.mult(whitePoint)));
        fpp.addFilter(new ContrastAdjustmentFilter(1f / 2.2f));
        fpp.addFilter(new SSAOFilter(3.1f,1.8f,0.2f,0.2f));
        fpp.addFilter(new FXAAFilter());
        viewPort.addProcessor(fpp);

    }

    @Override
    public void simpleUpdate(float tpf) {

        if (frames < 6) {
            // if (frames == 0) {
            //     NativeUtils.setProgress((this.progress += increment), "Preparing environment");
            // } else if (frames == 4) {
            //     NativeUtils.setProgress(1, "Ready");
            // }
            frames++;
        }

        if (autoRotate) {
            xRot -= tpf * rotationSpeed;
            chaseCam.setDefaultHorizontalRotation(xRot);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("MouseClick") && isPressed) {
            autoRotate = false;
        }
    }
}