package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btScalarArray extends JSObject {
    public int size();
    public float at(int index);
    
}
