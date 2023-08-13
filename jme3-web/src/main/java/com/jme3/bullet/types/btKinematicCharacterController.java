package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

 
public interface btKinematicCharacterController  extends btDestructible,JSObject{
    

    public void warp(btVector3 v);

    public void setWalkDirection(btVector3 v);

    public void setFallSpeed(float v);

    public void setJumpSpeed(float v);

    public void setGravity(btVector3 v);

    @Deprecated
    public default void setGravity(float v) {
        btVector3 vv = btUtils.newVector3(this,0, v, 0);
        setGravity(vv);
        btUtils.destroy(this,vv);
    }

    public void setMaxSlope(float v);

    public void setMaxJumpHeight(float v);

    public void setUp(btVector3 v);

    public btVector3 getUp();


    public btVector3 getWalkDirection();

    public float getFallSpeed();

    public float getJumpSpeed();

    public float getGravity();

    public float getMaxSlope();

    public float getMaxJumpHeight();

    public boolean onGround();

    public void jump();
    

    public default void setUpAxis(int axis) {
        btVector3 up = getUp();
        switch (axis) {
            case 0:
                up.setValue(1, 0, 0);
                break;
            case 1:
                up.setValue(0, 1, 0);
                break;
            case 2:
                up.setValue(0, 0, 1);
                break;
            default:
                throw new IllegalArgumentException("Invalid axis " + axis);
        }
        setUp(up);
    }
}
