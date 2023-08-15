package com.jme3.web.demo;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.web.context.NativeUtils;

public class TestPhysics extends SimpleApplication {
    public TestPhysics(AppState... initialStates) {
        super(initialStates);
    }

    /** Prepare the Physics Application State (jBullet) */
    private BulletAppState bulletAppState;

    /** Prepare Materials */
    private Material wall_mat;
    private Material stone_mat;
    private Material floor_mat;

    /** Prepare geometries for bricks and cannonballs. */
    private static final Box box;
    private static final Sphere sphere;
    private static final Box floor;

    /** dimensions used for bricks and wall */
    private static final float brickLength = 0.48f;
    private static final float brickWidth = 0.24f;
    private static final float brickHeight = 0.12f;

    static {
        /* Initialize the cannonball geometry */
        sphere = new Sphere(32, 32, 0.4f, true, false);
        sphere.setTextureMode(TextureMode.Projected);
        /* Initialize the brick geometry */
        box = new Box(brickLength, brickHeight, brickWidth);
        box.scaleTextureCoordinates(new Vector2f(1f, .5f));
        /* Initialize the floor geometry */
        floor = new Box(10f, 0.1f, 5f);
        floor.scaleTextureCoordinates(new Vector2f(3, 6));
    }
    float steps = 6;
    float increment = 1f / steps;
    float progress = 0;

    @Override
    public void simpleInitApp() {
        /* Set up Physics Game */
        NativeUtils.setProgress((this.progress += increment), "Loading Physics engine...");

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        /* Configure cam to look at scene */
        cam.setLocation(new Vector3f(0, 4f, 6f));
        cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
        /* Initialize the scene, materials, inputs, and physics space */
        NativeUtils.setProgress((this.progress += increment), "Init inputs");
        initInputs();
        NativeUtils.setProgress((this.progress += increment), "Init materials");

        initMaterials();
        NativeUtils.setProgress((this.progress += increment), "Init wall");

        initWall();
        NativeUtils.setProgress((this.progress += increment), "Init floor");

        initFloor();
        NativeUtils.setProgress((this.progress += increment), "Init crosshairs");

        initCrossHairs();
        NativeUtils.setProgress(1, "Ready");

    }

    @Override
    public void simpleUpdate(float tpf) {
        updateCrossHair();
        
    }

    /** Add InputManager action: Left click triggers shooting. */
    private void initInputs() {
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");
    }

    /**
     * Every time the shoot action is triggered, a new cannonball is produced.
     * The ball is set up to fly from the camera position in the camera
     * direction.
     */
    final private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("shoot") && !keyPressed) {
                makeCannonBall();
            }
        }
    };

    /** Initialize the materials used in this scene. */
    public void initMaterials() {
        wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        wall_mat.setTexture("ColorMap", tex);

        stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        stone_mat.setTexture("ColorMap", tex2);

        floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
    }

    /** Make a solid floor and add it to the scene. */
    public void initFloor() {
        Geometry floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, -0.1f, 0);
        this.rootNode.attachChild(floor_geo);
        /* Make the floor physical with mass 0.0f! */
        RigidBodyControl floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);
    }

    /** This loop builds a wall out of individual bricks. */
    public void initWall() {
        float startX = brickLength / 4;
        float height = 0;
        for (int j = 0; j < 15; j++) {
            for (int i = 0; i < 6; i++) {
                Vector3f vt = new Vector3f(i * brickLength * 2 + startX, brickHeight + height, 0);
                makeBrick(vt);
            }
            startX = -startX;
            height += 2 * brickHeight;
        }
    }

    /** Creates one physical brick. */
    private void makeBrick(Vector3f loc) {
        /* Create a brick geometry and attach it to the scene graph. */
        Geometry brick_geo = new Geometry("brick", box);
        brick_geo.setMaterial(wall_mat);
        rootNode.attachChild(brick_geo);
        /* Position the brick geometry. */
        brick_geo.setLocalTranslation(loc);
        /* Make brick physical with a mass > 0. */
        RigidBodyControl brick_phy = new RigidBodyControl(2f);
        /* Add physical brick to physics space. */
        brick_geo.addControl(brick_phy);
        bulletAppState.getPhysicsSpace().add(brick_phy);
    }

    /**
     * Creates one physical cannonball. By default, the ball is accelerated and
     * flies from the camera position in the camera direction.
     */
    public void makeCannonBall() {
        /* Create a cannonball geometry and attach to scene graph. */
        Geometry ball_geo = new Geometry("cannon ball", sphere);
        ball_geo.setMaterial(stone_mat);
        rootNode.attachChild(ball_geo);
        /* Position the cannonball. */
        ball_geo.setLocalTranslation(cam.getLocation());
        /* Make the ball physical with a mass > 0.0f */
        RigidBodyControl ball_phy = new RigidBodyControl(1f);
        /* Add physical ball to physics space. */
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        /* Accelerate the physical ball to shoot it. */
        ball_phy.setLinearVelocity(cam.getDirection().mult(25));
    }

    BitmapText ch;
    /** A plus sign used as crosshairs to help the player with aiming. */
    protected void initCrossHairs() {
        setDisplayStatView(false);
        // guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        ch = new BitmapText(guiFont);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // fake crosshairs :)
       
        guiNode.attachChild(ch);
    }

    protected void updateCrossHair() {
         ch.setLocalTranslation( // center
                settings.getWidth() / 2, settings.getHeight() / 2, 0);
    }
}
