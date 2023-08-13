package com.jme3.web.rendering;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public abstract class WebGLLintConfig implements JSObject {
    
    @JSProperty("maxDrawCalls")
    public abstract void setMaxDrawCalls(int maxDrawCalls);

    @JSProperty("failUnsetUniforms")
    public abstract void setFailUnsetUniforms(boolean failUnsetUniforms);

    @JSProperty("warnUndefinedUniforms")
    public abstract void setWarnUndefinedUniforms(boolean warnUndefinedUniforms);

    @JSProperty("throwOnError")
    public abstract void setThrowOnError(boolean throwOnError);

    @JSBody(script = "return {}")
    public static native WebGLLintConfig create();
    
}
