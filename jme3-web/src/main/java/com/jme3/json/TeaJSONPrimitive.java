package com.jme3.json;

import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSObjects;

import com.jme3.plugins.json.JsonPrimitive;

public class TeaJSONPrimitive extends TeaJSONElement implements JsonPrimitive {

    public TeaJSONPrimitive(JSObject element) {
        super(element);
    }

    @Override
    public boolean isNumber() {
        return JSObjects.typeOf(element).equals("number");
    }

    @Override
    public boolean isBoolean() {
        return JSObjects.typeOf(element).equals("boolean");
    }

    @Override
    public boolean isString() {
        return JSObjects.typeOf(element).equals("string");
    }
    
}
