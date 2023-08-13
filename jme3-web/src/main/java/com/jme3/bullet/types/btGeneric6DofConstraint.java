package com.jme3.bullet.types;

 
public interface btGeneric6DofConstraint extends btTypedConstraint {

    void setLinearUpperLimit(btVector3 convert);

    void setLinearLowerLimit(btVector3 v);

    void setAngularUpperLimit(btVector3 v);

    void setAngularLowerLimit(btVector3 v);

    btRotationalLimitMotor getRotationalLimitMotor(int i);

    btTranslationalLimitMotor getTranslationalLimitMotor();

     
}
