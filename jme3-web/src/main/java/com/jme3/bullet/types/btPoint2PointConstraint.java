package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;

public interface btPoint2PointConstraint extends btTypedConstraint {
    
    @JSMethod("get_m_setting")
    public btConstraintSetting getSetting();

}
