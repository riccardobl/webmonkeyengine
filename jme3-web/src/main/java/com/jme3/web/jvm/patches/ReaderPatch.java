package com.jme3.web.jvm.patches;

import java.io.IOException;
import java.nio.CharBuffer;

import org.teavm.classlib.java.nio.TCharBuffer;

public abstract class ReaderPatch {
    public abstract int read(char cbuf[], int off, int len) throws IOException;

    public int read(CharBuffer target) throws IOException {
        int len = target.remaining();
        char[] cbuf = new char[len];
        int n = read(cbuf, 0, len);
        if (n > 0)
            target.put(cbuf, 0, n);
        return n;
    }
}
