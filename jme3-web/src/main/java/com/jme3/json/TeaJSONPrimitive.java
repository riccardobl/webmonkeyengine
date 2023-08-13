package com.jme3.json;

import org.teavm.jso.JSObject;

import com.jme3.plugins.json.JsonPrimitive;

public class TeaJSONPrimitive extends TeaJSONElement implements JsonPrimitive {

    public TeaJSONPrimitive(JSObject element) {
        super(element);
    }
    
}
