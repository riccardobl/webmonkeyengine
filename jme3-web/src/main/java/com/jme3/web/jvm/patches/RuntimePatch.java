package com.jme3.web.jvm.patches;

import java.nio.ByteOrder;

import com.jme3.web.context.NativeUtils;

public class RuntimePatch {
    public static int availableProcessors() {
        return 4;
    }
    
}
