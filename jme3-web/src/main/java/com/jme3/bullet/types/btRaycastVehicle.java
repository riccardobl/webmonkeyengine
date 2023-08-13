package com.jme3.bullet.types;

import org.teavm.jso.JSObject;


public interface btRaycastVehicle extends JSObject {
    public void updateWheelTransform(int wheelIndex, boolean interpolatedTransform);
    
    public void setCoordinateSystem(int rightIndex, int upIndex, int forwardIndex);

    public btWheelInfo addWheel(btVector3 connectionPointCS0, btVector3 wheelDirectionCS0, btVector3 wheelAxleCS, float restLength, float radius, btVehicleTuning tuning, boolean frontWheel);

    
    public void applyEngineForce(float force, int wheel);

    public void setBrake(float brake, int wheel);

    public void setSteeringValue(float steering, int wheel);

    public void resetSuspension();

    public void setPitchControl(float pitch);

    public float getCurrentSpeedKmHour();

    public btVector3 getForwardVector();
    


}
