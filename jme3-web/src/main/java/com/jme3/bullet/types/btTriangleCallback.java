package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btTriangleCallback extends JSObject {
    
    @JSMethod("get_m_Triangles")
    public btVector3Array getTriangles();
    
}
