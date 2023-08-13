package com.jme3.web.jvm.patches;

import java.nio.ByteOrder;

import org.teavm.classlib.java.nio.TByteOrder;
import org.teavm.jso.JSBody;

import com.jme3.web.context.NativeUtils;

public class ByteOrderPatch {
    public static ByteOrder nativeOrder() {
        return NativeUtils.getNativeEndianess();
    }
    


}
