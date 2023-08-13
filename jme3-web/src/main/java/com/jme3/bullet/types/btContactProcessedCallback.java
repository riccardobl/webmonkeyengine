package com.jme3.bullet.types;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

@JSFunctor
public interface btContactProcessedCallback extends JSObject {

    public void callback(btManifoldPoint cp, btCollisionObject colObj0, btCollisionObject colObj1);
}
