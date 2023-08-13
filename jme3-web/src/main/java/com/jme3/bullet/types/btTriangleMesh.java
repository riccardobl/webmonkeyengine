package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btTriangleMesh extends JSObject {
    public void addTriangle(btVector3 v0, btVector3 v1, btVector3 v2, boolean removeDuplicateVertices);
    
    
}
