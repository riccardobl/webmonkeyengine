package com.jme3.web.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.teavm.jso.browser.Window;
import com.jme3.util.res.ResourcesLoaderImpl;

public class WebResourceLoaderImplNoCache implements ResourcesLoaderImpl {
    private static final Logger logger = Logger.getLogger(WebResourceLoaderImplNoCache.class.getName());

    public WebResourceLoaderImplNoCache() {

    }

    private String getFullPath(Class<?> clazz, String path) throws MalformedURLException {
        String resourcePath = path;
        if (clazz != null && !resourcePath.matches("^[a-zA-Z]+://.*")) {
            String className = clazz.getName();
            String classPath = className.replace('.', '/') + ".class";
            classPath = classPath.substring(0, classPath.lastIndexOf('/'));
            resourcePath = classPath + "/" + path;
        }

        return resourcePath;
    }
    
    private String getAbsPath(String resourcePath) throws MalformedURLException {

        String fullPath;

        // if fullUrl is absolute, then use it as is
        if (resourcePath.matches("^[a-zA-Z]+://.*")) {
            fullPath = resourcePath;
        } else {
            String loc = Window.current().getLocation().getFullURL();
            loc = loc.split("#")[0];
            loc = loc.split("\\?")[0];

            URL baseURL = new URL(loc);
            fullPath = new URL(baseURL, resourcePath).toString();
        }
        return fullPath;
    }

    @Override
    public URL getResource(String path, Class<?> clazz) {
        try {
            path = getFullPath(clazz, path);
            if (!exists(path)) return null;
            path=getAbsPath(path);
            return new URL(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path, Class<?> clazz) {
        try {
            URL url = getResource(path, clazz);
            if (url == null) return null;
            return url.openConnection().getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected volatile ArrayList<String> resourcesIndex;

    private ArrayList<String> getIndex() throws IOException{
        if (resourcesIndex == null) {
            try {
                ArrayList<String> index = new ArrayList<String>();
                String indexPath = this.getAbsPath(this.getFullPath(null, "resourcesIndex.txt"));
                logger.fine("Loading index "+indexPath);
                URL url = new URL(indexPath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                InputStream is = url.openStream();
                while ((len = is.read(buffer)) > -1) {
                    baos.write(buffer, 0, len);
                }
                baos.flush();
                String content = new String(baos.toByteArray(), Charset.forName("UTF-8"));
                String[] lines = content.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    String[] parts = line.split(" ", 2);
                    if (parts.length == 2) {
                        String hash = parts[0].trim(); // useless
                        String resource = parts[1].trim();
                        if (resource.length() > 0) {
                            index.add(resource);
                        }
                    }
                }
                resourcesIndex = index;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (resourcesIndex == null) {
            throw new IOException("Unable to load resources index.");
        }
        return resourcesIndex;
    } 

    private boolean exists(String path) throws IOException {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            // external file - always exists
            return true; 
        }
        if (path.startsWith("/")) path = path.substring(1);        
        for (String f : getIndex()) {
            if (f.startsWith("/")) f = f.substring(1);
            boolean v = f.equals(path);
            if (v) return true;
        }
        return false;
    }

    @Override
    public Enumeration<URL> getResources(String path, Class<?> clazz) throws IOException {
        ArrayList<String> index = getIndex();
        return Collections.enumeration(index.stream().filter(f -> f.endsWith(path)).map(f -> {
            try {
                return getResource(f, clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).filter(f -> f != null).collect(java.util.stream.Collectors.toList()));
    }

}
