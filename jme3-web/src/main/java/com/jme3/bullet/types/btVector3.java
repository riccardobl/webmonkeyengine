package com.jme3.bullet.types;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

import com.jme3.math.Vector3f;

 
public interface btVector3 extends JSObject {

    void setValue(float x, float y, float z);

    @JSMethod("x")
    float getX();

    @JSMethod("y")
    float getY();

    @JSMethod("z")
    float getZ();
       
}
