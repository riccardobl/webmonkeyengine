package com.jme3.bullet.gltf;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.GImpactCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.bounding.BoundingBox;
import com.jme3.plugins.json.JsonArray;
import com.jme3.plugins.json.JsonElement;
import com.jme3.plugins.json.JsonObject;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.plugins.gltf.ExtensionLoader;
import com.jme3.scene.plugins.gltf.GltfLoader;

public class BulletRigidbodyExtensionLoader implements ExtensionLoader {
    private static final Logger logger = Logger.getLogger(BulletRigidbodyExtensionLoader.class.getName());

    private Vector3f readV3(JsonObject jsonObject, String name) {
        Vector3f result = new Vector3f();
        JsonArray array = jsonObject.getAsJsonArray(name);
        result.x = array.get(0).getAsFloat();
        result.y = array.get(1).getAsFloat();
        result.z = array.get(2).getAsFloat();
        return result;
    }

    private static Vector3f getVector3f(VertexBuffer v, int id) {
        if (v == null || v.getNumElements() <= id) return new Vector3f();
        return new Vector3f((float) v.getElementComponent(id, 0), (float) v.getElementComponent(id, 1), (float) v.getElementComponent(id, 2));
    }

    private static BoundingBox getBoundingBox(Spatial n) {
        final Collection<VertexBuffer> meshes = new ArrayList<VertexBuffer>();
        n.depthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                if (spatial instanceof Geometry) {
                    VertexBuffer vb = ((Geometry) spatial).getMesh().getBuffer(Type.Position).clone();
                    // Vector3f scale = spatial.getWorldScale();
                    for (int i = 0; i < vb.getNumElements(); i++) {
                        vb.setElementComponent(i, 0, (float) vb.getElementComponent(i, 0) );
                        vb.setElementComponent(i, 1, (float) vb.getElementComponent(i, 1) );
                        vb.setElementComponent(i, 2, (float) vb.getElementComponent(i, 2) );
                    }
                    meshes.add(vb);
                }
            }
        });

        Vector3f min = null;
        Vector3f max = null;
        for (VertexBuffer vertices : meshes) {
            for (int i = 0; i < vertices.getNumElements(); i++) {
                Vector3f v = getVector3f(vertices, i);
                boolean c = false;
                if (min == null) {
                    c = true;
                    min = v.clone();
                }
                if (max == null) {
                    c = true;
                    max = v.clone();
                }
                if (c) continue;
                if (v.x < min.x) min.x = v.x;
                if (v.y < min.y) min.y = v.y;
                if (v.z < min.z) min.z = v.z;
                if (v.x > max.x) max.x = v.x;
                if (v.y > max.y) max.y = v.y;
                if (v.z > max.z) max.z = v.z;
            }
        }
        if (min == null || max == null) {
            min = new Vector3f();
            max = new Vector3f();
        }
        if (FastMath.abs(min.x - max.x) < 0.001) {
            min.x -= 0.001f;
            // max.x += 0.001f;
        }
        if (FastMath.abs(min.y - max.y) < 0.001) {
            min.y -= 0.001f;
            // max.y += 0.001f;
        }
        if (FastMath.abs(min.z - max.z) < 0.001) {
            min.z -= 0.001f;
            // max.z += 0.001f;
        }

        return new BoundingBox(min, max);
    }

    private static Collection<Float> getPoints(Mesh mesh) {
        FloatBuffer vertices = mesh.getFloatBuffer(Type.Position);
        vertices.rewind();
        int components = mesh.getVertexCount() * 3;
        ArrayList<Float> pointsArray = new ArrayList<Float>();
        for (int i = 0; i < components; i += 3) {
            pointsArray.add(vertices.get() );
            pointsArray.add(vertices.get());
            pointsArray.add(vertices.get() );
        }
        return pointsArray;
    }

    private CollisionShape createShape(String pshape, Spatial spatial, boolean dynamic) {
        CollisionShape collisionShape = null;
        switch (pshape) {
            case "MESH":

                final CompoundCollisionShape csh = new CompoundCollisionShape();
                Vector3f parentWorldTranslation = spatial.getWorldTranslation();
                Quaternion parentWorldRotation = spatial.getWorldRotation();
                Vector3f parentWorldScale = spatial.getWorldScale();                
                spatial.depthFirstTraversal(new SceneGraphVisitor() {
                    @Override
                    public void visit(Spatial s) {
                        if (s instanceof Geometry) {
                            Geometry g = (Geometry) s;
                            Mesh mesh = g.getMesh();
                            CollisionShape shape = null;
                            if (!dynamic) {
                                shape = new MeshCollisionShape(mesh);
                            } else {
                                shape = new GImpactCollisionShape(mesh);
                            }
                            if (shape != null) {
                                Vector3f childScale = g.getWorldScale().divide(parentWorldScale);
                                Vector3f childTranslation = g.getWorldTranslation().subtract(parentWorldTranslation).divide(parentWorldScale);
                                Quaternion childRotation = g.getWorldRotation().mult(parentWorldRotation.inverse());
                                shape.setScale(childScale);
                                csh.addChildShape(shape, childTranslation, childRotation.toRotationMatrix());                                
                            }
                        }
                    }
                });
                collisionShape = csh;
                break;

            case "SPHERE":
                Vector3f xtendsphere = getBoundingBox(spatial).getExtent(null);
                float radius = xtendsphere.x;
                if (xtendsphere.y > radius) radius = xtendsphere.y;
                if (xtendsphere.z > radius) radius = xtendsphere.z;
                collisionShape = new SphereCollisionShape(radius);
                break;

            case "HULL":
                final Collection<Float> points = new ArrayList<Float>();
                spatial.depthFirstTraversal(new SceneGraphVisitor() {
                    @Override
                    public void visit(Spatial s) {
                        if (s instanceof Geometry) {
                            Geometry g = (Geometry) s;
                            points.addAll(getPoints(g.getMesh()));
                        }
                    }
                });
                float primitive_arr[] = new float[points.size()];
                int i = 0;
                for (Float point : points) primitive_arr[i++] = (float) point;
                collisionShape = new HullCollisionShape(primitive_arr);
                break;

            case "BOX":
                BoundingBox bbox = getBoundingBox(spatial);
                collisionShape = new BoxCollisionShape(bbox.getExtent(null));
                break;

            case "CAPSULE":
                BoundingBox cbox = getBoundingBox(spatial);
                Vector3f xtendcapsule = cbox.getExtent(null);
                float r = (xtendcapsule.x > xtendcapsule.z ? xtendcapsule.x : xtendcapsule.z);
                collisionShape = new CapsuleCollisionShape(r, xtendcapsule.y * 2f - (r * 2f));
                break;

            case "CYLINDER":
                BoundingBox cybox = getBoundingBox(spatial);
                Vector3f xtendcylinder = cybox.getExtent(null);
                collisionShape = new CylinderCollisionShape(xtendcylinder, PhysicsSpace.AXIS_Y);
                break;

            case "CONE":
                BoundingBox cobox = getBoundingBox(spatial);
                Vector3f xtendcone = cobox.getExtent(null);
                collisionShape = new ConeCollisionShape((xtendcone.x > xtendcone.z ? xtendcone.x : xtendcone.z), xtendcone.y * 2f, PhysicsSpace.AXIS_Y);
                break;
            default:
                // Should never happen.
                logger.warning(pshape + " unsupported");
        }
        return collisionShape;
    }

    @Override
    public Object handleExtension(GltfLoader loader, String parentName, JsonElement parent, JsonElement extension, Object input) throws IOException {
        if (input instanceof Spatial) {
            System.out.println("CREATE RIGIDBODY FOR " + input);
            JsonObject jsonObject = extension.getAsJsonObject();
            String type = jsonObject.getAsJsonPrimitive("type").getAsString();
            boolean dynamic = jsonObject.getAsJsonPrimitive("dynamic").getAsBoolean();
            float mass = jsonObject.getAsJsonPrimitive("mass").getAsFloat();
            boolean isKinematic = jsonObject.getAsJsonPrimitive("isKinematic").getAsBoolean();
            float friction = jsonObject.getAsJsonPrimitive("friction").getAsFloat();
            float restitution = jsonObject.getAsJsonPrimitive("restitution").getAsFloat();
            float margin = jsonObject.getAsJsonPrimitive("margin").getAsFloat();
            float linearDamping = jsonObject.getAsJsonPrimitive("linearDamping").getAsFloat();
            float angularDamping = jsonObject.getAsJsonPrimitive("angularDamping").getAsFloat();
            Vector3f angularFactor = readV3(jsonObject, "angularFactor");
            Vector3f linearFactor = readV3(jsonObject, "linearFactor");
            String shape = jsonObject.getAsJsonPrimitive("shape").getAsString();
            int collisionMask = jsonObject.getAsJsonPrimitive("collisionMask").getAsInt();

            if (type == "PASSIVE") dynamic = false;
            if (!dynamic) mass = 0;
            else if (mass == 0) mass = 0.001f;
            if (margin < 0.01f) margin = 0.01f;

            CollisionShape collisionShape = createShape(shape, (Spatial) input, dynamic);
            collisionShape.setMargin(margin);

            Spatial spatial = (Spatial) input;
            new Exception().printStackTrace();
            if (spatial.getControl(RigidBodyControl.class) != null) throw new RuntimeException("Errpr");
            assert spatial.getControl(RigidBodyControl.class) == null;

            RigidBodyControl rb = new RigidBodyControl(collisionShape, mass);
            rb.setKinematic(isKinematic);
            rb.setFriction(friction);
            rb.setRestitution(restitution);
            rb.setAngularDamping(angularDamping);
            rb.setLinearDamping(linearDamping);
            rb.setAngularFactor(angularFactor);
            rb.setLinearFactor(linearFactor);
            rb.setCollisionGroup(collisionMask);
            rb.setKinematic(true);
            

            spatial.addControl(rb);
            if (!isKinematic) {
                spatial.addControl(new ActivateRigidbodyControl());
            }


        }

        return input;
    }

}
