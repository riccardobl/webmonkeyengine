package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btConstraintSetting extends JSObject {
    @JSMethod("get_m_tau")
    public float getTau();

    @JSMethod("set_m_tau")
    public void setTau(float tau);

    @JSMethod("get_m_damping")
    public float getDamping();

    @JSMethod("set_m_damping")
    public void setDamping(float damping);

    @JSMethod("get_m_impulseClamp")
    public float getImpulseClamp();

    @JSMethod("set_m_impulseClamp")
    public void setImpulseClamp(float impulseClamp);

    
}
