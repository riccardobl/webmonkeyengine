package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btOverlappingPairCache extends JSObject{

    void setInternalGhostPairCallback(btGhostPairCallback createGhostPairCallback);

     
}
