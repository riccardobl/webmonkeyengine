package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btTransform extends JSObject{
    
    public btVector3 getOrigin();
 
    public btMatrix3x3 getBasis();

    public btQuaternion getRotation();

    public void setBasis(btMatrix3x3 tmpM33);
}
