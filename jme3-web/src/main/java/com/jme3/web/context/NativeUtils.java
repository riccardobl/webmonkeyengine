package com.jme3.web.context;

import java.nio.ByteOrder;
import java.util.function.Consumer;

import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.canvas.ImageData;

 
public class NativeUtils {
    @JSBody(params = { "v", "message" }, script = "window.jme.setProgress(v,message);")
    public static native void setProgress(float v, String message);

    @JSBody(params = { "v", "message" }, script = "window.jme.increaseProgress(v,message);")
    public static native void increaseProgress(float v, String message);

    @JSBody(params={"obj"},script = "console.log(obj)")
    public static native String _js_print(JSObject obj);

    @JSBody(script = "return window.jme.canvas")
    public static native JSObject getCanvas();

    @JSFunctor
    private static interface LintCallback extends JSObject {
        public void callback();
    }
    
    @Async
    public static native void loadWebGLdebug();

    private static void loadWebGLdebug(AsyncCallback<Void> callback) {
        loadWebGLdebugAsync(() -> callback.complete(null));
    }

    @JSBody(params={"callback"},script = "window.jme.loadWebGLdebug(callback)")
    private static native void loadWebGLdebugAsync(LintCallback callback);


    @JSBody(script = "return window.jme.getEndian()")
    public static native String _js_endian();

    public static ByteOrder nativeEndianess;

    public static ByteOrder getNativeEndianess() {
        if (nativeEndianess != null) return nativeEndianess;
        String endian = _js_endian();
        if (endian.equals("little")) {
            nativeEndianess = ByteOrder.LITTLE_ENDIAN;
        } else if (endian.equals("big")) {
            nativeEndianess = ByteOrder.BIG_ENDIAN;
        } else {
            System.err.println("Unknown endianess: " + endian);
            nativeEndianess = ByteOrder.nativeOrder();
        }
        return nativeEndianess;
    }
    

    @JSBody(script = "return window.jme.getPostedMessages()")
    public static native String[] getPostedMessages();

    @JSBody(params={"o","name"},script = "if(!o.__SPECTOR_Metadata){o.__SPECTOR_Metadata={};};o.__SPECTOR_Metadata.name=name;")
    public static native void setSpectorMetaName(JSObject webglObject, String name);
}
