package com.jme3.web.audio;

import org.teavm.jso.JSBody;
import org.teavm.jso.webaudio.AudioContext;

public abstract class WebAudioContext extends AudioContext {
    @JSBody(params={"options"} , script="var Context = window.AudioContext || window.webkitAudioContext; return new Context(options);")
    public static native WebAudioContext create(WebAudioContextOptions options);


}
