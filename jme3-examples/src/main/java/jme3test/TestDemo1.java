package jme3test;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.environment.EnvironmentProbeControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;

public class TestDemo1 extends SimpleApplication {
     
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setGraphicsDebug(true);
    settings.setRenderer(AppSettings.LWJGL_OPENGL43);
    TestDemo1 app = new TestDemo1();
    app.setSettings(settings);
    app.start();
  }

    private BulletAppState bulletAppState;
    private float steps = 4;
    private float increment = 1f / steps;
    private float progress = 0;

    @Override
    public void simpleInitApp() {

        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/Path.hdr", SkyFactory.EnvMapType.EquirectMap);
        sky.setCullHint(CullHint.Never);
        rootNode.attachChild(sky);

        // // Create baker control
        EnvironmentProbeControl envProbe = new EnvironmentProbeControl(assetManager, 256);
        rootNode.addControl(envProbe);

        // // Tag the sky, only the tagged spatials will be rendered in the env map
        EnvironmentProbeControl.tag(sky);
        Node scene = (Node) assetManager.loadModel("demo1/test.glb");
        rootNode.attachChild(scene);

        MikktspaceTangentGenerator.generate(scene);
        // bulletAppState = new BulletAppState();
        // stateManager.attach(bulletAppState);
        // bulletAppState.setDebugEnabled(true);


        


        // bulletAppState.getPhysicsSpace().addAll(scene);


  
        cam.setLocation(new Vector3f(0, 4f, 6f));
        cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);

        flyCam.setMoveSpeed(200);

    }

    @Override
    public void simpleUpdate(float tpf) {
    }
}
