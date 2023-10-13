package com.jme.jmetx;

import java.util.logging.Level;

import com.jme3.app.SimpleApplication;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.ui.Picture;

public class Test extends SimpleApplication{

    public static void main(String[] args) {
        Test app = new Test();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // impost logger level fine
        JmeTxLoader.initFormats(renderManager.getRenderer());
        JmeTxLoader.registerLoader(assetManager,AWTLoader.class,"png","jpg","bmp");
        
        Picture pic = new Picture("pic");
        pic.setImage(assetManager,"/jmetxtest/test.png",true);
        pic.setWidth(128);
        pic.setHeight(128);
        pic.setPosition(settings.getWidth() / 2, settings.getHeight() / 2);
        guiNode.attachChild(pic);

        Picture pic2 = new Picture("pic2");
        pic2.setImage(assetManager,"/jmetxtest/test2.png",true);
        pic2.setWidth(128);
        pic2.setHeight(128);
        pic2.setPosition(settings.getWidth() / 2 + 128, settings.getHeight() / 2);
        guiNode.attachChild(pic2);
        


    }
    
}
