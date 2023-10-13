package com.jme3.web.filesystem;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nothings.stb.image.ColorComponents;
import org.nothings.stb.image.ImageResult;
import org.teavm.classlib.java.nio.TByteBuffer;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Uint16Array;
import org.teavm.jso.typedarrays.Uint8Array;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.TextureKey;
import com.jme3.math.FastMath;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.util.BufferUtils;

 
public class StbImageLoader implements AssetLoader {
    private static final Logger LOG = Logger.getLogger(StbImageLoader.class.getName());
    private void flipImage(byte[] img, int width, int height, int bpp) {
        int scSz = (width * bpp) / 8;
        byte[] sln = new byte[scSz];
        int y2 = 0;
        for (int y1 = 0; y1 < height / 2; y1++) {
            y2 = height - y1 - 1;
            System.arraycopy(img, y1 * scSz, sln, 0, scSz);
            System.arraycopy(img, y2 * scSz, img, y1 * scSz, scSz);
            System.arraycopy(sln, 0, img, y2 * scSz, scSz);
        }
    }

    private void flipImage(ByteBuffer img, int width, int height, int bpp) {
        try {
            byte[] arr = img.array();
            flipImage(arr, width, height, bpp);           
        }catch(Exception e){
            byte[] tmp = new byte[img.limit()];
            int pos = img.position();
            img.position(0);
            img.get(tmp);
            flipImage(tmp, width, height, bpp);
            img.position(0);
            img.put(tmp);
            img.position(pos);
        }
    }





    @JSFunctor
    private interface OnImageLoadCallback extends JSObject{
        public void complete();
    }
    
    @JSFunctor
    private interface ByteSupplier extends JSObject{
        public Uint8Array getBytes(int i);
    }



    @Async
    private static native void loadWithSTB(int len, ByteSupplier populateCallback,ImageData out);

    private static void loadWithSTB(int len, ByteSupplier populateCallback,ImageData out,AsyncCallback<Void> callback) {
        loadWithSTBAsync(len,populateCallback, out,() -> callback.complete(null));
    }

    @JSBody(params = { "len", "byteSupplier", "out" ,"callback"}, script = "window.StbImageLoad.load_image(len,byteSupplier,4,out,callback);")
    private static native void loadWithSTBAsync(int len, ByteSupplier byteSupplier, ImageData out, OnImageLoadCallback callback);




     
    @JSBody(params = { "data"}, script = "window.StbImageLoad.free(data);")
    private static native void freeSTB(  ImageData  data);


    private static abstract class ImageData implements JSObject {
        @JSProperty( "data")
        public  abstract Uint8ClampedArray getData();

        @JSProperty( "length")
        public  abstract int getLength();

        @JSProperty("lengthInBytes")
        public abstract int getLengthInBytes();

        @JSProperty( "width")
        public  abstract int getWidth();

        @JSProperty( "height")
        public  abstract int getHeight();

        @JSProperty( "bpc")
        public abstract int getBpc();
        
        @JSProperty( "bpp")
        public abstract int getBpp();

        @JSProperty("channels")
        public abstract int getNumChannels();
        
        @JSBody(script = "return {}")
        public static native ImageData create();
    }
 
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        Image img[]=new Image[1];
        Thread t = new Thread(() -> {
            synchronized (img) {

                try {
                    boolean flip = ((TextureKey) assetInfo.getKey()).isFlipY();
                    InputStream is = new BufferedInputStream(assetInfo.openStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }

                    byte[] barr = baos.toByteArray();
                    len = barr.length;

                    ImageData decodedData = ImageData.create();

                    Uint8Array uarr = Uint8Array.create(1024 * 10);

                    final int flen = len;
                    loadWithSTB(len, (i) -> {
                        int start = i;
                        int end = Math.min(i + uarr.getLength(), flen);
                        int length = end - start;
                        for (int j = 0; j < length; j++) {
                            uarr.set(j, barr[start + j]);
                        }
                        return uarr;
                    }, decodedData);

                    int imgW = decodedData.getWidth();
                    int imgH = decodedData.getHeight();
                    int bpc = decodedData.getBpc();
                    int bpp = decodedData.getBpp();
                    int length = decodedData.getLength();
                    int numChannels = decodedData.getNumChannels();
                    assert imgW * imgH * numChannels == length : "Invalid length :" + (imgW * imgH * numChannels) + " != " + length;

                    LOG.log(Level.FINE, "Loading image. Width: {0}, height: {1}, bpc: {2}, length: {3}", new Object[] { imgW, imgH, bpc, length });

                    ByteBuffer bbf;
                    if (bpc == 8) {
                        Uint8ClampedArray imgData = decodedData.getData();
                        bbf = BufferUtils.createByteBuffer(length);
                        for (int i = 0; i < length; i++) {
                            bbf.put((byte) imgData.get(i));
                            if (i % 100 == 0) {
                                Thread.yield();
                            }
                        }
                    } else if (bpc == 16) {
                        Uint16Array imgData = decodedData.getData().cast();
                        bbf = BufferUtils.createByteBuffer(length * 2);
                        float maxUshortValue = 65535;
                        for (int i = 0; i < length; i++) {
                            int u16 = imgData.get(i);
                            float f = (float) u16 / maxUshortValue;
                            short hf = FastMath.convertFloatToHalf(f);
                            bbf.putShort(hf);
                            if (i % 100 == 0) {
                                Thread.yield();
                            }
                        }
                    } else {
                        throw new IOException("Unsupported component sizeZ " + bpc);
                    }

                    freeSTB(decodedData);

                    bbf.flip();
                    if (flip) flipImage(bbf, imgW, imgH, bpp);
                    bbf.flip();

                    Format format;
                    switch (numChannels) {
                        case 4:
                            if (bpc == 8) {
                                format = Format.RGBA8;
                            } else {
                                format = Format.RGBA16F;
                            }
                            break;
                        case 3:
                            if (bpc == 8) {
                                format = Format.RGB8;
                            } else {
                                format = Format.RGB16F;
                            }
                            break;
                        case 2:
                            if (bpc == 8) {
                                format = Format.Luminance8Alpha8;
                            } else {
                                format = Format.Luminance16FAlpha16F;
                            }
                            break;
                        case 1:
                            if (bpc == 8) {
                                format = Format.Luminance8;
                            } else {
                                format = Format.Luminance16F;
                            }
                            break;
                        default:
                            throw new IOException("Unsupported number of channels: " + numChannels);
                    }

                    img[0] = new Image(format, imgW, imgH, bbf, null, com.jme3.texture.image.ColorSpace.sRGB);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    img.notifyAll();
                }
            }
        });

        t.setName("Loader " + assetInfo.getKey().getName());
        t.setDaemon(true);
        // t.setPriority(Thread.MAX_PRIORITY);
        t.start();
        synchronized (img) {
            try {
                img.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (img[0] == null) throw new IOException();
            return img[0];
        }
       
    }
    
}
