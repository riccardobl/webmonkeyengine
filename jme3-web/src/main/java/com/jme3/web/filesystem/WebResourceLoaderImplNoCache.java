package com.jme3.web.filesystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teavm.jso.JSBody;
import org.teavm.jso.browser.Window;
import org.teavm.jso.typedarrays.Int8Array;

import com.jme3.util.res.ResourcesLoaderImpl;

public class WebResourceLoaderImplNoCache implements ResourcesLoaderImpl {
    private static final Logger logger = Logger.getLogger(WebResourceLoaderImplNoCache.class.getName());
    private volatile byte preloadStatus = -1;

    public WebResourceLoaderImplNoCache() {
      
          
    }

 
    private String getFullPath(Class<?> clazz, String path) throws MalformedURLException {
        String resourcePath = path;
        if (clazz != null) {
            String className = clazz.getName();
            String classPath = className.replace('.', '/') + ".class";
            classPath = classPath.substring(0, classPath.lastIndexOf('/'));
            resourcePath = classPath + "/" + path;
        }
         URL baseURL = new URL(Window.current().getLocation().getFullURL());
        resourcePath = new URL(baseURL, resourcePath).toString();
 
        return resourcePath;
    }


    @Override
    public URL getResource(String path, Class<?> clazz) {
        try{
            path = getFullPath(clazz, path);
            
            return new URL(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path, Class<?> clazz) {
        URL url = getResource(path, clazz);
        if(url==null) return null;
        try {
            return url.openConnection().getInputStream();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String path, Class<?> clazz) throws IOException {
        URL url = getResource(path, clazz);
        if (url == null) {
            return Collections.emptyEnumeration();
        } else {
            return Collections.enumeration(Collections.singletonList(url));
        }
    }

}
