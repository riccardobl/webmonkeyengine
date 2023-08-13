package com.jme3.web.jvm.patches;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.teavm.jso.JSBody;

public abstract class ByteBufferPatch {
    public abstract byte[] array();

    // public abstract int position();

    // public abstract int position(int i);

    public abstract ByteBuffer put(byte[] src);
    public abstract ByteBuffer get(byte[] src);

    public ByteBuffer putDouble(double value) {
        long longValue = Double.doubleToLongBits(value);
        // byte[] bytes = this.array();
        // int p = this.position();
        // for (int i = 0; i < 8; i++) {
        //     bytes[i + p] = (byte) (longValue >> (56 - (i * 8)));
        // }
        byte[] bytes = new byte[8];
        // int p = this.position();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (longValue >> (56 - (i * 8)));
        }
        this.put(bytes);
        
        return (ByteBuffer) (Object) this;

    }

    public double getDouble() {
        byte[] bytes = new byte[8];
        this.get(bytes);
        long longValue = 0;
        for (int i = 0; i <  8; i++) {
            longValue |= (long) (bytes[i] & 0xFF) << (56 - (i * 8));
        }
        return Double.longBitsToDouble(longValue);
    }

    public ByteBuffer putFloat(float value) {
        // byte[] bytes = this.array();
        // int p = this.position();
        // int intValue = Float.floatToIntBits(value);
        // bytes[p + 0] = (byte) (intValue >> 24);
        // bytes[p + 1] = (byte) (intValue >> 16);
        // bytes[p + 2] = (byte) (intValue >> 8);
        // bytes[p + 3] = (byte) intValue;
        // this.position(p + 4);
        byte[] bytes = new byte[4];
        int intValue = Float.floatToIntBits(value);
        bytes[ 0] = (byte) (intValue >> 24);
        bytes[ 1] = (byte) (intValue >> 16);
        bytes[ 2] = (byte) (intValue >> 8);
        bytes[ 3] = (byte) intValue;
        this.put(bytes);
        return (ByteBuffer) (Object) this;
    }

    public ByteBuffer putFloat(int index, float value) {
        byte[] bytes = this.array();
        int p = index;
        int intValue = Float.floatToIntBits(value);
        bytes[p + 0] = (byte) (intValue >> 24);
        bytes[p + 1] = (byte) (intValue >> 16);
        bytes[p + 2] = (byte) (intValue >> 8);
        bytes[p + 3] = (byte) intValue;
        return (ByteBuffer) (Object) this;
    }

    public float getFloat() {
        // byte[] bytes = this.array();
        // int p = this.position();
        byte[] bytes = new byte[4];
        this.get(bytes);
        float value = bytes[ 0] << 24 | (bytes[ 1] & 0xFF) << 16 | (bytes[ 2] & 0xFF) << 8 | (bytes[ 3] & 0xFF);
        return value;
    }

    public float getFloat(int index) {
        byte[] bytes = this.array();
        float value = bytes[index + 0] << 24 | (bytes[index + 1] & 0xFF) << 16 | (bytes[index + 2] & 0xFF) << 8 | (bytes[index + 3] & 0xFF);
        return value;
    }

}
