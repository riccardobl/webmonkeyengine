package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btRigidBodyConstructionInfo extends JSObject{
    
    @JSMethod("set_m_mass")
    public void setMass(float mass);

    @JSMethod("set_m_motionState")
    public void setMotionState(btMotionState motionState);
 
    @JSMethod("get_m_mass")
    public float getMass();
    
    @JSMethod("get_m_motionState")
    public btMotionState getMotionState();

    @JSMethod("get_m_collisionShape")
    public btCollisionShape getCollisionShape();

    @JSMethod("set_m_collisionShape")
    public void setCollisionShape(btCollisionShape collisionShape);


    @JSMethod("get_m_friction")
    public float getFriction();

    @JSMethod("set_m_friction")
    public void setFriction(float friction);


    @JSMethod("set_m_linearDamping")
    public void setLinearDamping(float linearDamping);


    @JSMethod("get_m_linearDamping")
    public float getLinearDamping();

    @JSMethod("set_m_angularDamping")
    public void setAngularDamping(float angularDamping);

    @JSMethod("get_m_angularDamping")
    public float getAngularDamping();

    @JSMethod("set_m_restitution")
    public void setRestitution(float restitution);

    @JSMethod("get_m_restitution")
    public float getRestitution();

    @JSMethod("set_m_linearSleepingThreshold")
    public void setLinearSleepingThreshold(float linearSleepingThreshold);

    @JSMethod("get_m_linearSleepingThreshold")
    public float getLinearSleepingThreshold();

    @JSMethod("set_m_angularSleepingThreshold")
    public void setAngularSleepingThreshold(float angularSleepingThreshold);

    @JSMethod("get_m_angularSleepingThreshold")
    public float getAngularSleepingThreshold();
}
