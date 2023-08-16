package com.jme3.web.audio;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public abstract class WebAudioContextOptions implements JSObject{
    
    @JSProperty(value = "latencyHint")
    public abstract void setLatencyHint(String s);

    @JSProperty(value = "sampleRate")
    public abstract void setSampleRate(int i);
    
    @JSBody(script = "return {};")
    public static native WebAudioContextOptions create();
}
