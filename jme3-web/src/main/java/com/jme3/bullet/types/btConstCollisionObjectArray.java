package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btConstCollisionObjectArray extends JSObject {
    public int size();
    public btCollisionObject at(int index);
    
}
