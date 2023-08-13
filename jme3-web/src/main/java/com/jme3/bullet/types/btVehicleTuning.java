package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btVehicleTuning extends JSObject {

    @JSMethod("get_m_suspensionStiffness")
    public float getSuspensionStiffness();

    @JSMethod("set_m_suspensionStiffness")
    public void setSuspensionStiffness(float suspensionStiffness);

    @JSMethod("get_m_suspensionCompression")
    public float getSuspensionCompression();

    @JSMethod("set_m_suspensionCompression")
    public void setSuspensionCompression(float suspensionCompression);

    @JSMethod("get_m_suspensionDamping")
    public float getSuspensionDamping();

    @JSMethod("set_m_suspensionDamping")
    public void setSuspensionDamping(float suspensionDamping);

    @JSMethod("get_m_maxSuspensionTravelCm")
    public float getMaxSuspensionTravelCm();

    @JSMethod("set_m_maxSuspensionTravelCm")
    public void setMaxSuspensionTravelCm(float maxSuspensionTravelCm);

    @JSMethod("get_m_frictionSlip")
    public float getFrictionSlip();

    @JSMethod("set_m_frictionSlip")
    public void setFrictionSlip(float frictionSlip);

    @JSMethod("get_m_maxSuspensionForce")
    public float getMaxSuspensionForce();

    @JSMethod("set_m_maxSuspensionForce")
    public void setMaxSuspensionForce(float maxSuspensionForce);

    
}
