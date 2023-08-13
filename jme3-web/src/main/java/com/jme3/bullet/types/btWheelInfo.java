package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btWheelInfo extends JSObject {
    // btTransform m_worldTransform


    @JSMethod("get_m_worldTransform")
    public btTransform getWorldTransform();

    @JSMethod("get_m_raycastInfo")
    public btRaycastInfo getRaycastInfo();

    @JSMethod("get_m_skidInfo")
    public float getSkidInfo();

    @JSMethod("get_m_deltaRotation")
    public float getDeltaRotation();



    // setters
    @JSMethod("set_m_worldTransform")
    public void setWorldTransform(btTransform worldTransform);

    @JSMethod("set_m_suspensionStiffness")
    public void setSuspensionStiffness(float suspensionStiffness);

    @JSMethod("set_m_wheelsDampingCompression")
    public void setWheelsDampingCompression(float wheelsDampingCompression);

    @JSMethod("set_m_wheelsDampingRelaxation")
    public void setWheelsDampingRelaxation(float wheelsDampingRelaxation);

    @JSMethod("set_m_frictionSlip")
    public void setFrictionSlip(float frictionSlip);

    @JSMethod("set_m_rollInfluence")
    public void setRollInfluence(float rollInfluence);

    @JSMethod("set_m_maxSuspensionForce")
    public void setMaxSuspensionForce(float maxSuspensionForce);

    @JSMethod("set_m_maxSuspensionTravelCm")
    public void setMaxSuspensionTravelCm(float maxSuspensionTravelCm);

    @JSMethod("set_m_wheelsRadius")
    public void setWheelsRadius(float wheelsRadius);

    @JSMethod("set_m_bIsFrontWheel")
    public void setBIsFrontWheel(boolean isFrontWheel);

    @JSMethod("set_m_suspensionRestLength1")
    public void setSuspensionRestLength1(float suspensionRestLength1);


}
