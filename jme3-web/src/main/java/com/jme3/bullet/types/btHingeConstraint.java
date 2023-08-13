package com.jme3.bullet.types;

public interface btHingeConstraint extends btTypedConstraint{

    void enableAngularMotor(boolean enable, float targetVelocity, float maxMotorImpulse);


    void setLimit(float low, float high, float _softness, float _biasFactor, float _relaxationFactor);

    float getUpperLimit();


    float getLowerLimit();


    void setAngularOnly(boolean angularOnly);


    float getHingeAngle();


    boolean getEnableAngularMotor();


    float getMotorTargetVelocity();


    float getMaxMotorImpulse();
    
}
