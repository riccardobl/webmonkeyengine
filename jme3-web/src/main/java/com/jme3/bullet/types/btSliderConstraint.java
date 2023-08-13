package com.jme3.bullet.types;

public interface btSliderConstraint extends btTypedConstraint {

    float getLowerLinLimit();

    void setLowerLinLimit(float lowerLinLimit);

    float getUpperLinLimit();

    void setUpperLinLimit(float upperLinLimit);

    float getLowerAngLimit();

    void setLowerAngLimit(float lowerAngLimit);

    float getUpperAngLimit();

    void setUpperAngLimit(float upperAngLimit);

    float getSoftnessDirLin();

    void setSoftnessDirLin(float softnessDirLin);

    float getRestitutionDirLin();

    void setRestitutionDirLin(float restitutionDirLin);

    float getDampingDirLin();

    void setDampingDirLin(float dampingDirLin);

    float getSoftnessDirAng();

    void setSoftnessDirAng(float softnessDirAng);

    float getRestitutionDirAng();

    void setRestitutionDirAng(float restitutionDirAng);

    float getDampingDirAng();

    void setDampingDirAng(float dampingDirAng);

    float getSoftnessLimLin();

    void setSoftnessLimLin(float softnessLimLin);

    float getRestitutionLimLin();

    void setRestitutionLimLin(float restitutionLimLin);

    float getDampingLimLin();

    void setDampingLimLin(float dampingLimLin);

    float getSoftnessLimAng();

    void setSoftnessLimAng(float softnessLimAng);

    float getRestitutionLimAng();

    void setRestitutionLimAng(float restitutionLimAng);

    float getDampingLimAng();

    void setDampingLimAng(float dampingLimAng);

    float getSoftnessOrthoLin();

    void setSoftnessOrthoLin(float softnessOrthoLin);

    float getRestitutionOrthoLin();

    void setRestitutionOrthoLin(float restitutionOrthoLin);

    float getDampingOrthoLin();

    void setDampingOrthoLin(float dampingOrthoLin);

    float getSoftnessOrthoAng();

    void setSoftnessOrthoAng(float softnessOrthoAng);

    float getRestitutionOrthoAng();

    void setRestitutionOrthoAng(float restitutionOrthoAng);

    float getDampingOrthoAng();

    void setDampingOrthoAng(float dampingOrthoAng);

    boolean getPoweredLinMotor();

    void setPoweredLinMotor(boolean poweredLinMotor);

    float getTargetLinMotorVelocity();

    void setTargetLinMotorVelocity(float targetLinMotorVelocity);

    float getMaxLinMotorForce();

    void setMaxLinMotorForce(float maxLinMotorForce);

    boolean getPoweredAngMotor();

    void setPoweredAngMotor(boolean poweredAngMotor);

    float getTargetAngMotorVelocity();

    void setTargetAngMotorVelocity(float targetAngMotorVelocity);

    float getMaxAngMotorForce();

    void setMaxAngMotorForce(float maxAngMotorForce);
    
}
