package com.jme3.web.input;

 
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.events.WheelEvent;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;

import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.web.context.PointerLockOptions;
import com.jme3.web.context.WebCanvasElement;

public class WebMouseInput implements MouseInput, EventListener {
    private WebCanvasElement canvas;
    private boolean cursorVisible = false;
    private RawInputListener listener;
    private int xPos=0, yPos=0, wheelPos;
    private boolean undefinedPos = true;
    private boolean initialized = false;
    private final List<MouseMotionEvent> mouseMotionEvents = new ArrayList<>();
    private final List<MouseButtonEvent> mouseButtonEvents = new ArrayList<>();
    public WebMouseInput(WebCanvasElement canvas) {
        this.canvas = canvas;
    }

    @Override
    public void initialize() {
        Window win = Window.current();
        HTMLDocument doc = win.getDocument();
        doc.addEventListener("mousemove", this, false);
        doc.addEventListener("wheel", this, false);
        doc.addEventListener("mousedown", this, false);
        doc.addEventListener("mouseup", this, false);
        initialized = true;
    }

 
    @Override
    public void destroy() {
        Window win = Window.current();
        HTMLDocument doc = win.getDocument();
        doc.removeEventListener("mousemove", this, false);
        doc.removeEventListener("wheel", this, false);
        doc.removeEventListener("mousedown", this, false);
        doc.removeEventListener("mouseup", this, false);
        initialized = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public void handleEvent(Event evt) {
        if (listener == null) return;
        if (evt.getType().equals("mousemove")) {
            MouseEvent ev = (MouseEvent) evt;

            int dX;
            int dY;

            if (isLocked()&&!undefinedPos) { // captured pointer mode
                dX = (int) ev.getMovementX();
                dY =  (int)ev.getMovementY();              
                xPos += dX;
                yPos += dY;
            } else {
                int x =(int) ev.getClientX();
                int y =(int) ev.getClientY();
                

                x = canvas.getRelativePosX(x);
                y = canvas.getRelativePosY(y);

                dX = undefinedPos ? 0 : x - xPos;
                dY = undefinedPos ? 0 : y - yPos;
                xPos = x;
                yPos = y;
                undefinedPos = false;
            }

            MouseMotionEvent mme = new MouseMotionEvent((int) xPos, (int) yPos, (int) dX, (int) dY, 0, 0);
            mme.setTime(getInputTimeNanos());

            mouseMotionEvents.add(mme);
            ev.preventDefault();
        } else if (evt.getType().equals("wheel")) {
            WheelEvent ev=(WheelEvent) evt;
            float dX = 0;
            float dY = 0;
            double wheelDelta = ev.getDeltaY();
            int deltaMode = ev.getDeltaMode();
            wheelDelta = canvas.getPixelDeltaScrollY((float) wheelDelta, (int) deltaMode);
            wheelPos+=wheelDelta;
            
            MouseMotionEvent mme = new MouseMotionEvent((int) xPos, (int) yPos, (int) dX, (int) dY, (int) wheelPos, (int) wheelDelta);
            mme.setTime(getInputTimeNanos());

            mouseMotionEvents.add(mme);
            ev.preventDefault();
        } else if (evt.getType().equals("mousedown")) {
            MouseEvent ev = (MouseEvent) evt;
            int button = ev.getButton();
            boolean isPressed=true;
            int jmeButton = KeyMapper.jsMouseButtonToJme(button);
            if (jmeButton != -1) {
                MouseButtonEvent mbe = new MouseButtonEvent(jmeButton, isPressed, (int) xPos, (int) yPos);
                mbe.setTime(getInputTimeNanos());

                mouseButtonEvents.add(mbe);

            }
            ev.preventDefault();
        } else if (evt.getType().equals("mouseup")) {
            MouseEvent ev = (MouseEvent) evt;
            int button = ev.getButton();
            boolean isPressed=false;
            int jmeButton = KeyMapper.jsMouseButtonToJme(button);
            if (jmeButton != -1) {
                MouseButtonEvent mbe = new MouseButtonEvent(jmeButton, isPressed, (int) xPos, (int) yPos);
                mbe.setTime(getInputTimeNanos());

                mouseButtonEvents.add(mbe);
            }
            ev.preventDefault();
        }

    }
    
    private boolean isLocked() {
        return !cursorVisible;
    }

    @Override
    public void update() {
        boolean canvasCursorVisible = !canvas.isPointerLocked();
     

        if (cursorVisible != canvasCursorVisible) {
            boolean lock = !cursorVisible;
            undefinedPos = true;

            if (lock) {
                PointerLockOptions options = PointerLockOptions.create();
                // options.setUnadjustedMovement(true);
                canvas.requestPointerLock(options);
            } else canvas.exitPointerLock();
        }
        
        for (MouseMotionEvent mme : mouseMotionEvents) {
            listener.onMouseMotionEvent(mme);
        }
        mouseMotionEvents.clear();

        for (MouseButtonEvent mbe : mouseButtonEvents) {
            listener.onMouseButtonEvent(mbe);
        }
        mouseButtonEvents.clear();
    }



    @Override
    public void setInputListener(RawInputListener listener) {
        this.listener = listener;
    }

    @Override
    public long getInputTimeNanos() {
        return System.nanoTime();
    }

    @Override
    public void setCursorVisible(boolean visible) {
        cursorVisible=visible;
    }

    @Override
    public int getButtonCount() {
        return 3;
    }

    @Override
    public void setNativeCursor(JmeCursor cursor) {
        // unsupported
    }


    
}
