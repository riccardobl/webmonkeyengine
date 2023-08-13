 
package com.jme3.web.filesystem;

import com.jme3.asset.*;
import com.jme3.asset.plugins.UrlAssetInfo;
import com.jme3.system.JmeSystem;
import com.jme3.util.res.ResourcesLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;
 
public class WebLocator implements AssetLocator {

    private static final Logger logger = Logger.getLogger(WebLocator.class.getName());
    private String root = "";

    public WebLocator(){
    }

    @Override
    public void setRootPath(String rootPath) {
        this.root = rootPath;
        if (root.equals("/"))
            root = "";
        else if (root.length() > 1){
            if (root.startsWith("/")){
                root = root.substring(1);
            }
            if (!root.endsWith("/"))
                root += "/";
        }
    }
    
    @Override
    public AssetInfo locate(AssetManager manager, AssetKey key) {
        URL url;
                System.out.println("Locate" + key.getName());

        String name = key.getName();
        if (name.startsWith("/"))
            name = name.substring(1);

        name = root + name;

        url = ResourcesLoader.getResource( name);            
        

        
        if (url == null)
            return null;
      
        
             Exception ex=null;
            for (int i = 0; i < 3; i++) {
                try {
                    return UrlAssetInfo.create(manager, key, url);
                } catch (Exception e) {
                    System.out.println("Failed to load " + url + " " + e + ". Retry...");
                    ex = e;
                    try {
                        Thread.sleep(100);
                    } catch (Throwable e1) {
                 
                    }
                }
            }
            if (ex != null) {
                throw new AssetLoadException("Failed to read URL " + url, ex);                
            } else {
                throw new AssetLoadException("Failed to read URL " + url);                

            }
        
    }
}
