
package com.jme3.web.context;

import com.jme3.input.JoyInput;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.TouchInput;
import com.jme3.input.dummy.DummyKeyInput;
import com.jme3.input.dummy.DummyMouseInput;
import com.jme3.json.TeaJSONParser;
import com.jme3.opencl.Context;
import com.jme3.plugins.json.Json;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.system.*;
import com.jme3.util.res.ResourcesLoader;
import com.jme3.web.context.WebCanvasElement;
import com.jme3.web.filesystem.WebResourceLoaderImpl;
import com.jme3.web.input.WebKeyInput;
import com.jme3.web.input.WebMouseInput;
import com.jme3.web.input.WebTouchInput;
import com.jme3.web.rendering.WebGL;
import com.jme3.web.rendering.WebGLOptions;
import com.jme3.web.rendering.WebGLWrapper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.xml.Document;


public class WebContext implements JmeContext, Runnable {
   

    protected static final Logger logger = Logger.getLogger(WebContext.class.getName());

    protected static final String THREAD_NAME = "jME3 Headless Main";

    protected AtomicBoolean created = new AtomicBoolean(false);
    protected AtomicBoolean needClose = new AtomicBoolean(false);
    protected final Object createdLock = new Object();

    protected int frameRate;
    protected AppSettings settings = new AppSettings(true);
    protected Timer timer;
    protected SystemListener listener;
    protected Renderer renderer;
    protected WebCanvasElement canvas;
    protected boolean autofit=false;

    @Override
    public Type getType() {
        return Type.Display;
    }

    /**
     * Accesses the listener that receives events related to this context.
     *
     * @return the pre-existing instance
     */
    @Override
    public SystemListener getSystemListener() {
        return listener;
    }

    @Override
    public void setSystemListener(SystemListener listener) {
        this.listener = listener;
    }

    private long timeThen;
    private long timeLate;

    public void sync(int fps) {
        long timeNow;
        long gapTo;
        long savedTimeLate;

        gapTo = timer.getResolution() / fps + timeThen;
        timeNow = timer.getTime();
        savedTimeLate = timeLate;

        try {
            while (gapTo > timeNow + savedTimeLate) {
                Thread.sleep(1);
                timeNow = timer.getTime();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (gapTo < timeNow) {
            timeLate = timeNow - gapTo;
        } else {
            timeLate = 0;
        }

        timeThen = timeNow;
    }


    private void doInit() {
       
        
        timer = new NanoTimer();
        // settings
        
        Window window=Window.current();
        Document doc = window.getDocument();

        
        logger.fine("Searching viable canvas...");
      
        WebCanvasElement canvas = (WebCanvasElement) doc.querySelector("canvas#jme");
        if (canvas == null) {
            logger.fine("Canvas not found, create a new one.");
            canvas = (WebCanvasElement) doc.createElement("canvas");
            canvas.setId("jme");
            doc.getElementsByTagName("body").get(0).appendChild(canvas);
        }


        this.canvas = canvas;
        logger.fine("Fetching context...");
        
        if (settings.getWidth() == -1 || settings.getHeight() == -1) {
            canvas.setWidth(canvas.getClientWidth());
            canvas.setHeight(canvas.getClientHeight());
        } else {
            canvas.setWidth(settings.getWidth());
            canvas.setHeight(settings.getHeight());
        }
        
        

        setTitle(settings.getTitle());
        // if (settings.isGammaCorrection()) {
   
        // }
        WebGLOptions attrs =  WebGLOptions.create();
        String colorSpace="srgb";
        attrs.setColorSpace(colorSpace);
        attrs.setDrawingColorSpace(colorSpace);
        attrs.setUnpackColorSpace(null);
        attrs.setPowerPreference("high-performance");
        attrs.setDepth(true);
        attrs.setAlpha(false);
        attrs.setDesynchronized(true);
        attrs.setPremultipliedAlpha(false);
        attrs.setPreserveDrawingBuffer(true);
        

        attrs.setAntialias(settings.getSamples()>1);

        WebGLWrapper ctx = (WebGLWrapper) canvas.getContext("webgl2", attrs);
        if (ctx == null) {
            throw new RuntimeException("WebGL2 not supported");
        }
        ctx.pixelStorei(WebGLWrapper.UNPACK_COLORSPACE_CONVERSION_WEBGL, WebGLWrapper.NONE);

        logger.fine("Starting WebGL renderer...");
        WebGL gl = new WebGL(ctx);
        renderer = new GLRenderer(gl, gl, gl);
        renderer.initialize();
        gl.setCaps(renderer.getCaps());



        logger.fine("sRGB: "+settings.isGammaCorrection());
        // renderer.setMainFrameBufferSrgb(settings.isGammaCorrection()); UNSUPPORTED?
        renderer.setLinearizeSrgbImages(settings.isGammaCorrection());
            
        logger.fine("WebGL renderer started!");
        System.out.println("Init Initialize "+listener);

        listener.initialize();
        logger.fine("WebGL created!");

    }

    private void doDestroy() {
          listener.destroy();
        timer = null;
   
        logger.fine("WebGL destroyed.");
    }

    private void loop() {
        listener.update();
        //  if (frameRate > 0) {
        //         sync(frameRate);
        //     }
        Window.requestAnimationFrame((t) -> {
            loop();
           
        });
    }
    
    @Override
    public void run() {
        
 
        

        doInit();

        // loop();
        
        // try {
        //     Thread.sleep(999999999);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        do {
            if (settings.isResizable()) {
                
                canvas.canvasFitParent();
                int w = canvas.getClientWidth();
                int h = canvas.getClientHeight();
                if (w != settings.getWidth() || h != settings.getHeight()) {
                    settings.setResolution(w, h);
         
                    listener.reshape(w, h);
                }
            }
            
            if (settings.isFullscreen()) {
                if (!canvas.isFullscreen()) {
                    canvas.requestFullscreen();
                }
            } else {
                if (canvas.isFullscreen()) {
                    canvas.exitFullscreen();
                }
                
            }
            
            listener.update();

            if (frameRate > 0) {
                sync(frameRate);
            }
        } while (!needClose.get());

        doDestroy();
    }

    @Override
    public void destroy(boolean waitFor) {
        needClose.set(true);
     }

    @Override
    public void create(boolean waitFor) {
        if (created.get()) {
            logger.warning("create() called when WebGL context is already created!");
            return;
        }
        new Thread(this, THREAD_NAME).start();

    }

    @Override
    public void restart() {
    }

    @Override
    public void setAutoFlushFrames(boolean enabled) {
    }

    @Override
    public MouseInput getMouseInput() {
        return new WebMouseInput(canvas);
    }

    @Override
    public KeyInput getKeyInput() {
        return new WebKeyInput(canvas);
    }

    @Override
    public JoyInput getJoyInput() {
        return null;
    }

    @Override
    public TouchInput getTouchInput() {
        return new WebTouchInput(canvas, settings);
    }

    @Override
    public void setTitle(String title) {
        Window.current().setName(title);
        
    }

    public void create() {
        create(false);
    }

    public void destroy() {
        destroy(false);
    }

    protected void waitFor(boolean createdVal) {
        synchronized (createdLock) {
            while (created.get() != createdVal) {
                try {
                    createdLock.wait();
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    @Override
    public boolean isCreated() {
        return created.get();
    }

    @Override
    public void setSettings(AppSettings settings) {
        this.settings.copyFrom(settings);
        frameRate = settings.getFrameRate();
        if (frameRate <= 0) frameRate = 60; // use default update rate.
    }

    @Override
    public AppSettings getSettings() {
        return settings;
    }

    @Override
    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public boolean isRenderable() {
        return true; // Doesn't really matter if true or false. Either way
                     // RenderManager won't render anything.
    }

    @Override
    public Context getOpenCLContext() {
        return null;
    }

    /**
     * Returns the height of the framebuffer.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public int getFramebufferHeight() {
        throw new UnsupportedOperationException("null context");
    }

    /**
     * Returns the width of the framebuffer.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public int getFramebufferWidth() {
        throw new UnsupportedOperationException("null context");
    }

    /**
     * Returns the screen X coordinate of the left edge of the content area.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public int getWindowXPosition() {
        throw new UnsupportedOperationException("null context");
    }

    /**
     * Returns the screen Y coordinate of the top edge of the content area.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public int getWindowYPosition() {
        throw new UnsupportedOperationException("null context");
    }
}
