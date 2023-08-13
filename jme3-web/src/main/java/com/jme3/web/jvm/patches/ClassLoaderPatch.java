package com.jme3.web.jvm.patches;

import java.net.URL;

public class ClassLoaderPatch {
    public URL getResource(String name) {
        throw new UnsupportedOperationException("Not supported. Use Resources.getResource instead");
    }
}
