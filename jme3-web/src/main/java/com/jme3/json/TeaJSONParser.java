package com.jme3.json;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.jme3.plugins.json.*;
 
public class TeaJSONParser implements JsonParser {

    @Override
    public JsonObject parse(InputStream stream) {

        InputStreamReader reader = new InputStreamReader(stream);
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        try {
            while ((len = reader.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String jsonString = sb.toString();

        return new TeaJSONObject( org.teavm.jso.json.JSON.parse(jsonString).cast());
        
    }

}
