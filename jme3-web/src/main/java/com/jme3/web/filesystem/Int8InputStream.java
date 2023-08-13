package com.jme3.web.filesystem;

import java.io.IOException;
import java.io.InputStream;

import org.teavm.jso.typedarrays.Int8Array;

public class Int8InputStream extends InputStream {
    private Int8Array i8;
    private int pos;
    public Int8InputStream(Int8Array i8) {
        this.i8 = i8;
        this.pos = 0;
    }

    @Override
    public int read() throws IOException {
        if (pos == i8.getLength()) {
            return -1;
        }
        byte b = i8.get(pos);
        pos++;
        return b & 0xFF;
    }
    
    
}
