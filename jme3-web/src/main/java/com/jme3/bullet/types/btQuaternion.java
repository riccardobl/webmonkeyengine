package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btQuaternion  extends JSObject {
    float getX();

    float getY();

    float getZ();

    float getW();

    void setX(float v);

    void setY(float v);

    void setZ(float v);

    void setW(float v);
    

}
