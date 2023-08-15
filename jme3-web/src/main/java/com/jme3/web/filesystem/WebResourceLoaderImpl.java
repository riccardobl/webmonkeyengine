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

public class WebResourceLoaderImpl implements ResourcesLoaderImpl {
    private static final Logger logger = Logger.getLogger(WebResourceLoaderImpl.class.getName());
    private volatile byte preloadStatus = -1;

    public WebResourceLoaderImpl() {
      
          
    }

    void prefetch() {
        while (preloadStatus == 1) {
            try {
                System.out.println("Waiting for prefetch");
                Thread.sleep(100);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (preloadStatus == 0) return;
        preloadStatus = 1;

        boolean res=WebResourceLoaderWrapper.jmeResourcesPrefetch();
        System.out.println("Done " + res);
        preloadStatus = 0;
    }

    private String getFullPath(Class<?> clazz, String path) {
        String resourcePath = path;
        if (clazz != null) {
            String className = clazz.getName();
            String classPath = className.replace('.', '/') + ".class";
            classPath = classPath.substring(0, classPath.lastIndexOf('/'));
            resourcePath = classPath + "/" + path;
        }
 
        return resourcePath;
    }


    private URL newURL(String fullPath) throws MalformedURLException {
        URL baseURL = new URL(Window.current().getLocation().getFullURL());
        String fullURL = new URL(baseURL, fullPath).toString();
        
        URL url = new URL(null, fullURL, new URLStreamHandler() {
             @Override
            protected URLConnection openConnection(final URL u) throws IOException {
                Int8Array entry = WebResourceLoaderWrapper.jmeResourcesGetEntry(fullPath);
                if (entry == null) {
                    throw new IOException("Resource not found: " + fullPath);
                }
              
                Int8InputStream stream = new Int8InputStream(entry);

                return new URLConnection(null) {
                    @Override
                    public void connect() throws IOException {
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return stream;
                    }
                };
            }
        });
        return url;
    }

    @Override
    public URL getResource(String path, Class<?> clazz) {
        prefetch();
        path = getFullPath(clazz, path);
        logger.log(Level.FINE, "Fetch resource {0}", path);
        try {
            return newURL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path, Class<?> clazz) {
        prefetch();
        path = getFullPath(clazz, path);
        logger.log(Level.FINE, "Fetch resource as stream {0}", path);
        try {
            Int8Array entry = WebResourceLoaderWrapper.jmeResourcesGetEntry(path);
            if (entry == null) return null;
            Int8InputStream stream = new Int8InputStream(entry);
            return stream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String path, Class<?> clazz) throws IOException {
        prefetch();
        List<URL> urls = new ArrayList<URL>();
        try {
            String index[] = WebResourceLoaderWrapper.jmeResourcesGetIndex();
            for (String resource : index) {
                resource = resource.trim();
                if (resource.length() > 0 && resource.endsWith(path)) {
                    URL url = getResource(resource, null);
                    urls.add(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.FINE, "Found {0} resources in {1}", new Object[] { urls.size(), path });
        return Collections.enumeration(urls);
    }

}
