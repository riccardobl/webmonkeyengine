package com.jme3.web.filesystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;

public class PrefetchedHttpZipLocator implements AssetLocator {
    private static final Logger logger = Logger.getLogger(PrefetchedHttpZipLocator.class.getName());

        
    private static Map<String,WrappedZip> zipCache=new ConcurrentHashMap<String,WrappedZip>();

    private static class WrappedZip{
        private WrappedEntry entries[];
        final AtomicInteger users = new AtomicInteger(0);
        private final String key;

        public WrappedEntry getEntry(String path) {
            for (WrappedEntry entry : entries) {
                if (entry.getPath().equals(path)) {
                    return entry;
                }
            }
            return null;
        }

        WrappedZip(String key,WrappedEntry[] entries) {
            this.key = key;
            this.entries = entries;

        }
        
        String getKey() {
            return key;
        }
    }

    private static class WrappedEntry {
        private final byte entryData[];
        private final String pathInZip;

        WrappedEntry(String pathInZip, byte[] data) {
            this.pathInZip = pathInZip;
            this.entryData = data;
        }

        String getPath() {
            return pathInZip;
        }

        InputStream openStream() {
            return new ByteArrayInputStream(entryData);
        }

    }
    
    private static class ZipAssetInfo extends AssetInfo {

        private final WrappedEntry entry;

        public ZipAssetInfo(AssetManager manager, AssetKey key, WrappedEntry entry) {
            super(manager, key);
            this.entry = entry;
        }

        @Override
        public InputStream openStream() {
            try {
                return entry.openStream();
            } catch (Exception ex) {
                throw new AssetLoadException("Failed to load zip entry: " + entry, ex);
            }
        }
    }
    
     private WrappedZip nwZip;


    @Override
    public void setRootPath(String rootPath) {
        nwZip = zipCache.computeIfAbsent(rootPath, k -> {
            try {
                logger.fine("Prefetching zip file: " + rootPath);
                URL url = new URL(rootPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int code = conn.getResponseCode();
                if (code == 404)   return null;                
                InputStream is = conn.getInputStream();
                ZipInputStream zipStream = new ZipInputStream(is);
                ZipEntry entry;
                ArrayList<WrappedEntry> wEntries=new ArrayList<WrappedEntry>();
                while ((entry = zipStream.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        continue;
                    }
                    String pathInZip = entry.getName();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte chunk[] = new byte[2048];
                    int read;
                    while ((read = zipStream.read(chunk)) != -1) {
                        baos.write(chunk, 0, read);
                    }
                    byte entryData[] = baos.toByteArray();
                    baos.close();
                    wEntries.add(new WrappedEntry(pathInZip, entryData));
                }

                zipStream.close();
                logger.fine("Extracted "+wEntries.size()+" zip entries");

                WrappedZip wZip = new WrappedZip(
                    rootPath,
                    wEntries.toArray(new WrappedEntry[0])
                );
                
                return wZip;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        });

        if (nwZip == null) {
            logger.warning("Failed to open zip file: " + rootPath);
            // throw new AssetLoadException("Failed to open zip file: " + rootPath);
            return;
        }

        nwZip.users.incrementAndGet();

    }

    @Override
    public void finalize() {
        if (nwZip != null) {
            if (nwZip.users.decrementAndGet() == 0) {
                zipCache.remove(nwZip.getKey());
            }
        }
    }

    @Override
    public AssetInfo locate(AssetManager manager, AssetKey key) {
        if (nwZip == null) return null;
        String name = key.getName();
        if(name.startsWith("/"))name=name.substring(1);
        WrappedEntry entry = nwZip.getEntry(name);
        if(entry==null)return null;
        return new ZipAssetInfo(manager, key, entry);
    }

}
