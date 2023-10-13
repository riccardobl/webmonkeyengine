package com.jme3.web.filesystem;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.nothings.stb.image.ColorComponents;
import org.nothings.stb.image.ImageResult;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.TextureKey;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.util.BufferUtils;

public class JStbImageLoader  implements AssetLoader {
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
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        try {
            boolean flip = ((TextureKey) assetInfo.getKey()).isFlipY();
            InputStream is = new BufferedInputStream(assetInfo.openStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }

            

            ImageResult ir = ImageResult.FromData(baos.toByteArray(), ColorComponents.RedGreenBlueAlpha);
            int w = ir.getWidth();
            int h = ir.getHeight();
            int bpc = ir.getBitsPerChannel();
            byte data[] = ir.getData();
            if(flip)flipImage(data, w, h, bpc*4);
            ByteBuffer bbf = BufferUtils.createByteBuffer(data.length);
            bbf.put(data);
            bbf.flip();
            



            

                    
            
            Image img = new Image(Format.RGBA8, w, h, bbf, null, com.jme3.texture.image.ColorSpace.sRGB);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
    
}
