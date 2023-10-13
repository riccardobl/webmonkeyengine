package com.jme3.web.filesystem;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
public class WebAssetInfo extends AssetInfo {
    
    final private URL url;
    private InputStream in;
    
    public static WebAssetInfo create(AssetManager assetManager, AssetKey key, URL url) throws IOException {
        // Check if URL can be reached. This will throw
        // IOException which calling code will handle.
        //   HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //   conn.setUseCaches(false);
        //   conn.setDoOutput(false);
        //   conn.setDoInput(false);
        // int code = conn.getResponseCode();
        // if (code == 404) {
        //     return null;
        // }
        
        // conn.disconnect();
        return new WebAssetInfo(assetManager, key, url);
        
    }
    
    private WebAssetInfo(AssetManager assetManager, AssetKey key, URL url) throws IOException {
        super(assetManager, key);
        this.url = url;
    }
    
    // public boolean hasInitialConnection(){
    //     return in != null;
    // }
    
    @Override
    public InputStream openStream() {
        if (in != null){
            // Reuse the already existing stream (only once)
            InputStream in2 = in;
            in = null;
            return in2;
        }else{
            // Create a new stream for subsequent invocations.
            try {
                URLConnection conn = url.openConnection();
                conn.setUseCaches(false);
                return conn.getInputStream();
            } catch (IOException ex) {
                throw new AssetLoadException("Failed to read URL " + url, ex);
            }
        }
    }
}