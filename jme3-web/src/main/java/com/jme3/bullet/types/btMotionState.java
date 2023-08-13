package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btMotionState extends JSObject{

    void getWorldTransform(btTransform worldTrans);
    void setWorldTransform(btTransform worldTrans);


}
