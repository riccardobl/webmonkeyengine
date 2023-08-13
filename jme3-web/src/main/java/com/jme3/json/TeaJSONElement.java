package com.jme3.json;

import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSBoolean;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.core.JSString;

import com.jme3.plugins.json.*;



public class TeaJSONElement implements JsonElement {
    protected org.teavm.jso.JSObject element;

    public TeaJSONElement( org.teavm.jso.JSObject  element) {
        this.element = element;
    }

    @Override
    public String getAsString() {
        JSString str=element.cast();
        return str.stringValue();
    }

    @Override
    public JsonObject getAsJsonObject() {
         
        return new TeaJSONObject(element.cast());
    }

    @Override
    public float getAsFloat() {
        JSNumber num=element.cast();
        return num.floatValue();
    }

    @Override
    public int getAsInt() {
        JSNumber num=element.cast();
        return num.intValue();
    }

    @Override
    public boolean getAsBoolean() {
        JSBoolean bool=element.cast();
        return bool.booleanValue();
    }

    @Override
    public JsonArray getAsJsonArray() {
        JSArray<?> arr=element.cast();
        return new TeaJSONArray(arr);
    }
    
}
