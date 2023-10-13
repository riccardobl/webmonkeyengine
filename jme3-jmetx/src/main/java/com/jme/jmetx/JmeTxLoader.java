package com.jme.jmetx;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.renderer.Caps;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Image.Format;

public class JmeTxLoader implements AssetLoader {
    private final static Logger logger = Logger.getLogger(JmeTxLoader.class.getName());
    private static ThreadLocal<ArrayList<Format>> compatibleFormats = new ThreadLocal<ArrayList<Format>>();

    private static ThreadLocal<Map<String,AssetLoader>> imageLoader = ThreadLocal.withInitial(()->{
        return new HashMap<String,AssetLoader>();
    });

    public static void registerLoader(AssetManager assetManager, Class<? extends AssetLoader> loader, String... extensions) {
        assetManager.registerLoader(JmeTxLoader.class, extensions);
        
        try {
            AssetLoader obj = loader.getDeclaredConstructor().newInstance();
            for (String ext : extensions) {
                imageLoader.get().put(ext, obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void initFormats(Renderer renderer) {
        ArrayList<Format> cformats = new ArrayList<Format>();

        EnumSet<Caps> caps = renderer.getCaps();
        cformats.clear();
         
        if (caps.contains(Caps.TextureCompressionETC2)) {
            logger.info("ETC2 supported");
            cformats.add(Format.ETC2);
            cformats.add(Format.ETC2_ALPHA1);
            cformats.add(Format.ETC1);
        } else if (caps.contains(Caps.TextureCompressionETC1)) {
            logger.info("ETC1 supported");
            cformats.add(Format.ETC1);
        }
      
         if (caps.contains(Caps.TextureCompressionS3TC)) {
            logger.info("S3TC supported");
            cformats.add(Format.DXT1);
            cformats.add(Format.DXT1A);
            cformats.add(Format.DXT3);
            cformats.add(Format.DXT5);
        }

        if (caps.contains(Caps.TextureCompressionBPTC)) {
            logger.info("BPTC supported");
            cformats.add(Format.BC6H_SF16);
            cformats.add(Format.BC6H_UF16);
            cformats.add(Format.BC7_UNORM);
            cformats.add(Format.BC7_UNORM_SRGB);
        }
        if (caps.contains(Caps.TextureCompressionRGTC)) {
            logger.info("RGTC supported");
            cformats.add(Format.RGTC1);
            cformats.add(Format.RGTC2);
        }

        compatibleFormats.set(cformats);
    }

 
 
    private Image loadJmeTx(AssetKey key, AssetManager manager,Format f) {
        try {

            String txPath = key.getName();
            if(txPath.startsWith("/")) txPath = txPath.substring(1);
            String jmetxPath = "/jmetx/" + txPath + ".jmetx";
            AssetInfo info = manager.locateAsset(new AssetKey(jmetxPath));
            if (info == null) throw new IOException("Cannot locate jmetx root file " + jmetxPath);

            BinaryImporter importer = BinaryImporter.getInstance();
            InputStream is = info.openStream();
            JmeTxRoot jmetxRoot = (JmeTxRoot) importer.load(is);

            String jmetxSelectedPath = jmetxRoot.get(f);
            if (jmetxSelectedPath == null) throw new IOException("Cannot locate jmetx image with format " + f);

            AssetInfo jmetxSelectedInfo = manager.locateAsset(new AssetKey(jmetxSelectedPath));
            if (jmetxSelectedInfo == null) throw new IOException("Cannot locate jmetx image " + jmetxSelectedInfo);

            InputStream jmetxSelectedIs = jmetxSelectedInfo.openStream();
            Image img = (Image) importer.load(jmetxSelectedIs);
            logger.info(jmetxSelectedPath + " loaded");

            return img;

        } catch (IOException e) {
            logger.info("Cannot load jmetx image " + key.getName() + ": " + e.getMessage());
        }
        return null;

    }
    
    

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        AssetManager manager = assetInfo.getManager();
        Image out = null;
        for (Format f : compatibleFormats.get()) {
            out = loadJmeTx(assetInfo.getKey(), manager, f);
            if(out!=null)return out;
        }
        logger.info("Cannot load jmetx image " + assetInfo.getKey().getName() + " no compatible format found. Load base image.");
        AssetLoader loader = imageLoader.get().get(assetInfo.getKey().getExtension());
        if (loader == null) {
            logger.warning("jmetx can't find fallback loader for " + assetInfo.getKey().getExtension() + " please register a loader with JmeTxLoader.registerLoader()");
            return null;
        }         
        return  (Image) loader.load(assetInfo);
        
    }

    

    
}
