package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btCollisionObject extends JSObject {
    public static int ACTIVE_TAG = 1;

    public static int ISLAND_SLEEPING = 2;

    public static int WANTS_DEACTIVATION = 3;

    public static int DISABLE_DEACTIVATION = 4;

    public static int DISABLE_SIMULATION = 5;

    public int getUserIndex();

    public void setUserIndex(int index);

    int getCollisionFlags();

    void setCollisionFlags(int f);

    btTransform getInterpolationWorldTransform();

    void setActivationState(int state);

    void setCcdSweptSphereRadius(float radius);

    void setCcdMotionThreshold(float threshold);

    float getCcdSweptSphereRadius();

    float getCcdMotionThreshold();

    float getCcdSquareMotionThreshold();

    float getFriction();

    void setFriction(float frict);

    float getRestitution();

    void setRestitution(float rest);

    void activate();

    void activate(boolean forceActivation);

    boolean isActive();

    void setCollisionShape(btCollisionShape collisionShape);


    btTransform getWorldTransform();

    void setWorldTransform(btTransform worldTrans);

    public default void setUserPointer(Object obj) {
        int index = btUtils.setUserPointer(obj);
        setUserIndex(index);

    }
    
    public default Object getUserPointer() {
        int index = getUserIndex();
        return btUtils.getUserPointer(index);
    }

    public default void clearUserPointer() {
        int index = getUserIndex();
        btUtils.clearUserPointer(index);

    }

}

