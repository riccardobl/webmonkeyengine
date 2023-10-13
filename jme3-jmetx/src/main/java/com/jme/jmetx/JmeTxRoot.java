package com.jme.jmetx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.texture.Image.Format;

public class JmeTxRoot implements Savable {
    private final Map<Integer, String> formatPathMap = new HashMap<>();

    public String get(Format f) {
        return formatPathMap.get(f.ordinal());
    }

     void add(Format f, String path) {
        formatPathMap.put(f.ordinal(), path);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        int formats[] = new int[formatPathMap.size()];
        String paths[] = new String[formatPathMap.size()];
        int i = 0;
        for (Entry<Integer, String> entry : formatPathMap.entrySet()) {
            formats[i] = entry.getKey();
            paths[i] = entry.getValue();
            i++;
        }
        oc.write(formats, "formats", null);
        oc.write(paths, "paths", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        int formats[] = ic.readIntArray("formats", null);
        String paths[] = ic.readStringArray("paths", null);
        assert formats.length == paths.length;
        assert formats!=null&&paths!=null;
        if (formats != null && paths != null) {
            for (int i = 0; i < formats.length; i++) {
                formatPathMap.put(formats[i], paths[i]);
            }
        }
        
    }
    
    
}
