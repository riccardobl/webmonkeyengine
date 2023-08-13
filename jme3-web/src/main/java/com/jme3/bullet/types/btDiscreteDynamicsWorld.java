package com.jme3.bullet.types;

public interface btDiscreteDynamicsWorld extends btDynamicsWorld{

    btOverlappingPairCache getPairCache();

    void stepSimulation(float time, int maxSteps, float accuracy);

    void addCollisionObject(btPairCachingGhostObject btObject);

    void removeCollisionObject(btPairCachingGhostObject btObject);

    void addAction(btKinematicCharacterController btController);

    void removeAction(btKinematicCharacterController btController);

    void addRigidBody(btRigidBody btObject);

    void addVehicle(btRaycastVehicle btObject);

    void removeVehicle(btRaycastVehicle btObject);

    void removeRigidBody(btRigidBody btObject);

    void addConstraint(btTypedConstraint btObject, boolean b);

    void removeConstraint(btTypedConstraint btObject);

    btVector3 getGravity();

    void applyGravity();

    void clearForces();
    
    btContactSolverInfo getSolverInfo();

    void addCollisionObject(btPairCachingGhostObject btObject, int characterFilter, short s);

    void rayTest(btVector3 convert, btVector3 convert2, btAllHitsRayResultCallback callback);
    // void convexSweepTest([Const] btConvexShape castShape, [Const, Ref]
    // btTransform from, [Const, Ref] btTransform to, [Ref] ConvexResultCallback
    // resultCallback, float allowedCcdPenetration);
    void convexSweepTest(btCollisionShape castShape, btTransform from, btTransform to, btClosestConvexResultCallback resultCallback, float allowedCcdPenetration);

}
