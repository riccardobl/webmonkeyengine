package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btBroadphaseInterface  extends JSObject{

    btOverlappingPairCache getOverlappingPairCache();
    
}
