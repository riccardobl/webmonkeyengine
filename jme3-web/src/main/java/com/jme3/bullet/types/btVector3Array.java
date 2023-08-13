package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btVector3Array extends JSObject {
    public int size();
    public btVector3 at(int index);
    
}
