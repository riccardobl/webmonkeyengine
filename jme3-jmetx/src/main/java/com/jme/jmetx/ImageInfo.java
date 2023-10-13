package com.jme.jmetx;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

public class ImageInfo {
     final boolean isSrgb;
     final boolean hasAlpha;

     private String readString(File file) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         FileInputStream fis = new FileInputStream(file);
         byte[] buffer = new byte[1024];
         int read = 0;
         while ((read = fis.read(buffer, 0, buffer.length)) != -1) {
             baos.write(buffer, 0, read);
         }
         fis.close();
         baos.close();
            return new String(baos.toByteArray(), "UTF-8");
            
        }

        ImageInfo(String path) {
            boolean isSrgb = true;
            boolean hasAlpha = true;
            try {
                BufferedImage img = ImageIO.read(new File(path));
                hasAlpha = img.getColorModel().hasAlpha();

                String colorSpacePath = path + ".colorSpace";
                File colorSpaceFile = new File(colorSpacePath);

                if (colorSpaceFile.exists()) {
                    try {
                        String colorSpace = readString(colorSpaceFile);
                        isSrgb = colorSpace.contains("srgb");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    path = path.toLowerCase();

                    isSrgb = path.contains("_normal") || path.contains("_metal") || path.contains("_rough") || path.contains("_ao") || path.contains("_parallax")
                            || path.contains("_height");
                }

            } catch (Exception e) {
                e.printStackTrace();

            }

            this.isSrgb = isSrgb;
            this.hasAlpha = hasAlpha;

        }
}
