package com.jme3.web.jvm.patches;

import java.lang.ref.Reference;

public class ReferenceQueuePatch {
    public Reference remove() throws InterruptedException {
        return null;
    }
}
