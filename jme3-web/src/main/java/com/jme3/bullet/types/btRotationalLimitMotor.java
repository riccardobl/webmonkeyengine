package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btRotationalLimitMotor extends JSObject {

    @JSMethod("get_m_loLimit")
    public float getLoLimit();

    @JSMethod("set_m_loLimit")
    public void setLoLimit(float value);

    @JSMethod("get_m_hiLimit")
    public float getHiLimit();

    @JSMethod("set_m_hiLimit")
    public void setHiLimit(float value);

    @JSMethod("get_m_targetVelocity")
    public float getTargetVelocity();

    @JSMethod("set_m_targetVelocity")
    public void setTargetVelocity(float value);

    @JSMethod("get_m_maxMotorForce")
    public float getMaxMotorForce();

    @JSMethod("set_m_maxMotorForce")
    public void setMaxMotorForce(float value);

    @JSMethod("get_m_enableMotor")
    public boolean getEnableMotor();

    @JSMethod("set_m_enableMotor")
    public void setEnableMotor(boolean value);

    @JSMethod("get_m_bounce")
    public float getBounce();

    @JSMethod("set_m_bounce")
    public void setBounce(float value);

    @JSMethod("get_m_stopERP")
    public float getERP();

    @JSMethod("set_m_stopERP")
    public void setERP(float value);

    @JSMethod("get_m_stopCFM")
    public float getStopCFM();

    @JSMethod("set_m_stopCFM")
    public void setStopCFM(float value);

    @JSMethod("get_m_maxLimitForce")
    public float getMaxLimitForce();

    @JSMethod("set_m_maxLimitForce")
    public void setMaxLimitForce(float value);

    @JSMethod("get_m_damping")
    public float getDamping();

    @JSMethod("set_m_damping")
    public void setDamping(float value);

    @JSMethod("get_m_limitSoftness")
    public float getLimitSoftness();

    @JSMethod("set_m_limitSoftness")
    public void setLimitSoftness(float value);
}
