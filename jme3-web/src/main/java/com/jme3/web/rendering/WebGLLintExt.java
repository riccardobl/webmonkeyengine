package com.jme3.web.rendering;

import org.teavm.jso.JSObject;

public interface WebGLLintExt extends JSObject {
    
    public void setConfiguration(WebGLLintConfig config);

    public void disable();

    
    public void tagObject(JSObject object, String tag);
    
}
