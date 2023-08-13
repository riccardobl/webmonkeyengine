package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

import org.teavm.jso.JSMethod;
 
public interface btTranslationalLimitMotor extends JSObject {

    @JSMethod("get_m_lowerLimit")
    public btVector3 getLowerLimit();
    
    @JSMethod("set_m_lowerLimit")
    public void setLowerLimit(btVector3 value);
    
    @JSMethod("get_m_upperLimit")
    public btVector3 getUpperLimit();
    
    @JSMethod("set_m_upperLimit")
    public void setUpperLimit(btVector3 value);
    
    @JSMethod("get_m_accumulatedImpulse")
    public btVector3 getAccumulatedImpulse();
    
    @JSMethod("set_m_accumulatedImpulse")
    public void setAccumulatedImpulse(btVector3 value);
    
    @JSMethod("get_m_limitSoftness")
    public float getLimitSoftness();
    
    @JSMethod("set_m_limitSoftness")
    public void setLimitSoftness(float value);
    
    @JSMethod("get_m_damping")
    public float getDamping();
    
    @JSMethod("set_m_damping")
    public void setDamping(float value);
    
    @JSMethod("get_m_restitution")
    public float getRestitution();
    
    @JSMethod("set_m_restitution")
    public void setRestitution(float value);
    
}