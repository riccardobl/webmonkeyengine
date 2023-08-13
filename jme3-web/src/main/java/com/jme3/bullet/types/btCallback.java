package com.jme3.bullet.types;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.events.Event;

@JSFunctor
public interface btCallback extends JSObject {
    void run();


}
