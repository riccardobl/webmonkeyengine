package com.jme.jmetx;

import com.jme3.texture.Image.Format;

public class CompressonatorFormatMap {
      public final Format jmeFormat;
        public final String cpFormat;
        public final boolean hasAlpha;
        public final boolean isSrgb;

        CompressonatorFormatMap(Format jmeFormat, String cpFormat, boolean hasAlpha, boolean isSrgb) {
            this.jmeFormat = jmeFormat;
            this.cpFormat = cpFormat;
            this.hasAlpha = hasAlpha;
            this.isSrgb = isSrgb;
        }
}
