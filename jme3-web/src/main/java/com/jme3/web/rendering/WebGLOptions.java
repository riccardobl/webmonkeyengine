package com.jme3.web.rendering;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSProperty;
import org.teavm.jso.webgl.WebGLContextAttributes;

public abstract class WebGLOptions extends WebGLContextAttributes{
    
    @JSProperty(value="colorSpace")
    public abstract void setColorSpace(String colorSpace);

    @JSProperty(value="unpackColorSpace")
    public abstract void setUnpackColorSpace(String colorSpace);

    @JSProperty(value="powerPreference")
    public abstract void setPowerPreference(String hint);

    @JSProperty(value="drawingBufferColorSpace")
    public abstract void setDrawingColorSpace(String colorSpace);

    @JSBody(script = "return {};")
    public static native WebGLOptions create();

    @JSProperty(value="desynchronized")
    public abstract void setDesynchronized(boolean b);
    
}
