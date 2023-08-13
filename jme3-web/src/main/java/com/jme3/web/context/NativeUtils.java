package com.jme3.web.context;

import java.nio.ByteOrder;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

 
public class NativeUtils {
    @JSBody(params = { "v", "message" }, script = "window.jme.setProgress(v,message);")
    public static native void setProgress(float v, String message);

    @JSBody(params={"obj"},script = "console.log(obj)")
    public static native String _js_print(JSObject obj);
    

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

    
}
