package com.jme3.json;

import java.util.Set;
import java.util.Map.Entry;

import org.teavm.jso.core.JSMapLike;
import org.teavm.jso.core.JSObjects;

import com.jme3.plugins.json.*;



public class TeaJSONObject extends TeaJSONElement implements JsonObject {
 
    public TeaJSONObject(JSMapLike gsonObject) {
        super(gsonObject);
    }

    private JSMapLike obj() {
        return (JSMapLike) element;
    }

    @Override
    public JsonArray getAsJsonArray(String string) {
        if (!JSObjects.hasProperty(element, string)) return null;
        return new TeaJSONArray(((org.teavm.jso.JSObject)obj().get(string)).cast());
    }

    @Override
    public JsonObject getAsJsonObject(String string) {
        if (!JSObjects.hasProperty(element, string)) return null;
        return new TeaJSONObject(((org.teavm.jso.JSObject)obj().get(string)).cast());
    }

    @Override
    public boolean has(String string) {        
        return JSObjects.hasProperty(element, string);
    }

    @Override
    public JsonElement get(String string) {
        if (!JSObjects.hasProperty(element, string)) return null;
        return new TeaJSONElement(((org.teavm.jso.JSObject)obj().get(string)));
        
    }

    @Override
    public Entry<String, JsonElement>[] entrySet() {
        String keys[]=JSObjects.getOwnPropertyNames(obj());
        Entry<String, JsonElement>[] entries = new Entry[keys.length];
        int i = 0;
        for (String key: keys) {

            Entry<String, JsonElement> e = new Entry<String, JsonElement>() {
                @Override
                public String getKey() {
                    return key;
                }

                @Override
                public TeaJSONElement getValue() {
                    return new TeaJSONElement((org.teavm.jso.JSObject)obj().get(key));
                }

                @Override
                public TeaJSONElement setValue(JsonElement value) {
                    throw new UnsupportedOperationException("Unimplemented method 'setValue'");
                }
            };

            entries[i++] = e;
        }
        return entries;
        
    }

    @Override
    public JsonPrimitive getAsJsonPrimitive(String string) {
        if (!JSObjects.hasProperty(element, string)) return null;
        return new TeaJSONPrimitive(((org.teavm.jso.JSObject)obj().get(string)).cast());
    }
}