/*
 * Copyright (c) 2009-2023 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.web.context;

import com.jme3.audio.AudioRenderer;
import com.jme3.json.TeaJSONParser;
import com.jme3.plugins.json.Json;
import com.jme3.system.JmeSystemDelegate;
import com.jme3.system.Platform;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeContext.Type;
import com.jme3.util.BufferAllocatorFactory;
import com.jme3.util.res.ResourcesLoader;
import com.jme3.web.audio.WebAudioRenderer;
import com.jme3.web.filesystem.WebResourceLoaderImplNoCache;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class JmeWebSystem extends JmeSystemDelegate {
    protected final static Logger logger = Logger.getLogger(JmeWebSystem.class.getName());

    public JmeWebSystem() {
        super();
        if (System.getProperty(ResourcesLoader.PROPERTY_RESOURCES_LOADER_IMPLEMENTATION) == null) {
            System.setProperty(ResourcesLoader.PROPERTY_RESOURCES_LOADER_IMPLEMENTATION, WebResourceLoaderImplNoCache.class.getName());
        }
        if (System.getProperty(Json.PROPERTY_JSON_PARSER_IMPLEMENTATION) == null) {
            System.setProperty(Json.PROPERTY_JSON_PARSER_IMPLEMENTATION, TeaJSONParser.class.getName());
        }
        if (System.getProperty(BufferAllocatorFactory.PROPERTY_BUFFER_ALLOCATOR_IMPLEMENTATION) == null) {
            System.setProperty(BufferAllocatorFactory.PROPERTY_BUFFER_ALLOCATOR_IMPLEMENTATION, HeapAllocator.class.getName());
        }

        ResourcesLoader.setImpl(new WebResourceLoaderImplNoCache());
        Json.setParser(TeaJSONParser.class);
        


        System.out.println(System.getProperty(ResourcesLoader.PROPERTY_RESOURCES_LOADER_IMPLEMENTATION));
        System.out.println(System.getProperty(Json.PROPERTY_JSON_PARSER_IMPLEMENTATION));
        System.out.println(System.getProperty(BufferAllocatorFactory.PROPERTY_BUFFER_ALLOCATOR_IMPLEMENTATION));
        
    }
    
    @Override
    public void writeImageFile(OutputStream outStream, String format, ByteBuffer imageData, int width, int height) throws IOException {
        logger.warning("Unimplemented method 'writeImageFile'");
    }

    @Override
    public URL getPlatformAssetConfigURL() {

                return ResourcesLoader.getResource("/Web.cfg");

     }

    @Override
    public JmeContext newContext(AppSettings settings, Type contextType) {
        initialize(settings);
        JmeContext ctx = new WebContext();
        ctx.setSettings(settings);
        return ctx;
    }

    @Override
    public AudioRenderer newAudioRenderer(AppSettings settings) {
        return new WebAudioRenderer();
    }

    @Override
    public void initialize(AppSettings settings) {
        logger.info("Initialize jme web");
    }

    @Override
    public void showSoftKeyboard(boolean show) {
       logger.warning("Unimplemented method 'showSoftKeyboard'");
    }



    public Platform getPlatform() {      
        return Platform.Web;      
    }
}
