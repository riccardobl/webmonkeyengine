package com.jme3.web.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
            e.printStackTrace();
        }
        return null;
    }

    protected ArrayList<URL> index;

    @Override
    public Enumeration<URL> getResources(String path, Class<?> clazz) throws IOException {
        if (index == null) {
            String indexPath=this.getFullPath(clazz, "resourcesIndex.txt");
            URL url=new URL(indexPath);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                InputStream is = url.openStream();
                while ((len = is.read(buffer)) > -1 ) {
                    baos.write(buffer, 0, len);
                }
                baos.flush();
                String content = new String(baos.toByteArray(), Charset.forName("UTF-8"));
                String[] lines = content.split("\n");
                index = new ArrayList<URL>();
                for (String line : lines) {
                    line = line.trim();
                    String[] parts=line.split(" ",1);
                    if(parts.length==2){
                        String hash=parts[0].trim(); // useless
                        String resource=parts[1].trim();
                        if (resource.length() > 0 ) {
                            URL url2 = getResource(line, clazz);
                            index.add(url2);
                        }
                    }
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Collections.enumeration(index.stream().filter(f -> f.toString().endsWith(path)).collect(java.util.stream.Collectors.toList()));
    }

}
