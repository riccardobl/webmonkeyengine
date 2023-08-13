package com.jme3.bullet.types;
 
public interface btConeTwistConstraint  extends btTypedConstraint{

    void setLimit(float swingSpan1, float swingSpan2, float twistSpan);

    void setAngularOnly(boolean value);
    
}
