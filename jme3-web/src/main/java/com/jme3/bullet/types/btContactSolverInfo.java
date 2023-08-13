package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btContactSolverInfo extends JSObject{

    @JSMethod("get_m_numIterations")
    int getNumIterations();

    @JSMethod("set_m_numIterations")
    void setNumIterations(int numIterations);
    
}
