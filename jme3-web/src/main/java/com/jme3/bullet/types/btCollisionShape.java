package com.jme3.bullet.types;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

import com.jme3.math.Vector3f;

 
public interface btCollisionShape extends JSObject {
    
    void calculateLocalInertia(float mass, btVector3 inertia);

    void setLocalScaling(btVector3 scaling);

    float getMargin();

    void setMargin(float margin);
}
