package com.jme3.json;


import java.util.Iterator;

import org.teavm.jso.core.JSArray;

import com.jme3.plugins.json.*;



public class TeaJSONArray extends TeaJSONElement implements JsonArray {
    public TeaJSONArray(JSArray element) {
        super(element);
    }

    private JSArray arr() {
        return element.cast();
    }

    @Override
    public Iterator<JsonElement> iterator() {
        return new Iterator<JsonElement>() {
            int i = 0;
            

            @Override
            public boolean hasNext() {
                return i < arr().getLength();
            }

            @Override
            public JsonElement next() {
                return new TeaJSONElement((org.teavm.jso.JSObject)arr().get(i++)).autoCast();
            }
        };
    }

    @Override
    public JsonElement get(int i) {
        return new TeaJSONElement((org.teavm.jso.JSObject)arr().get(i)).autoCast();
    }

    @Override
    public int size() {
        return arr().getLength();
    }
    
}
