package com.jme3.web.demo;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.environment.EnvironmentProbeControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.util.SkyFactory;
import com.jme3.web.context.NativeUtils;

public class TestDemo1 extends SimpleApplication {
    public TestDemo1(AppState... initialStates) {
        super(initialStates);
    }

  

    private BulletAppState bulletAppState;
    private float steps = 4;
    private float increment = 1f / steps;
    private float progress = 0;

    @Override
    public void simpleInitApp() {

        NativeUtils.setProgress((this.progress += increment), "Loading skybox");
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/Path.hdr", SkyFactory.EnvMapType.EquirectMap);
        rootNode.attachChild(sky);

        // Create baker control
        EnvironmentProbeControl envProbe = new EnvironmentProbeControl(assetManager, 256);
        rootNode.addControl(envProbe);

        // Tag the sky, only the tagged spatials will be rendered in the env map
        EnvironmentProbeControl.tag(sky);

        NativeUtils.setProgress((this.progress += increment), "Loading Physics engine...");

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);


        NativeUtils.setProgress((this.progress += increment), "Load scene");
        
        Node scene = (Node) assetManager.loadModel("demo1/test.glb");
        rootNode.attachChild(scene);

        bulletAppState.getPhysicsSpace().addAll(scene);

        NativeUtils.setProgress((this.progress += increment), "Init materials");

  
        cam.setLocation(new Vector3f(0, 4f, 6f));
        cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);

        NativeUtils.setProgress(1, "Ready");
        flyCam.setMoveSpeed(20);

    }

    @Override
    public void simpleUpdate(float tpf) {
    }
}
