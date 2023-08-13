package com.jme3.web.input;

import java.util.ArrayList;
import java.util.List;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLDocument;

import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.TouchInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.system.AppSettings;
import com.jme3.web.context.WebCanvasElement;

public class WebTouchInput implements TouchInput,EventListener{
    private WebCanvasElement canvas;
    private RawInputListener listener;
    private boolean initialized = false;
    private boolean simulateMouse;
    private boolean keyboardEventsEnabled = false;
    private boolean flipX = false;
    private boolean flipY = false;

    private static class TouchStatus {
        boolean undefinedPos;
        int xPos, yPos;


    }
    private final List<TouchStatus> touchStatus = new ArrayList<>();
    private final List<MouseMotionEvent> mouseMotionEvents = new ArrayList<>();
    private final List<MouseButtonEvent> mouseButtonEvents = new ArrayList<>();
    
    private final List<TouchEvent> touchEvents = new ArrayList<>();
    public WebTouchInput(WebCanvasElement canvas,AppSettings settings) {
        this.canvas = canvas;
        this.setSimulateMouse(settings.isEmulateMouse());
        this.setSimulateKeyboard(settings.isEmulateKeyboard());
        flipX = settings.isEmulateMouseFlipX();
        flipY = settings.isEmulateMouseFlipY();
        
    }

    @Override
    public void initialize() {
        Window win = Window.current();
        HTMLDocument doc = win.getDocument();
        doc.addEventListener("touchstart", this, true);
        doc.addEventListener("touchmove", this, true);
        doc.addEventListener("touchcancel", this, true);
        doc.addEventListener("touchend", this, true);
        initialized = true;
    }
 

    @Override
    public void update() {
        for (TouchEvent te : touchEvents) {
            listener.onTouchEvent(te);
        }
        touchEvents.clear();

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
    public void destroy() {
        Window win = Window.current();
        HTMLDocument doc = win.getDocument();
        doc.removeEventListener("touchstart", this, true);
        doc.removeEventListener("touchmove", this, true);
        doc.removeEventListener("touchcancel", this, true);
        doc.removeEventListener("touchend", this, true);
        initialized = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
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
    public void setSimulateMouse(boolean simulate) {
      simulateMouse=simulate;
    }

    @Override
    public boolean isSimulateMouse() {
        return simulateMouse;
    }

    @Override
    public void setSimulateKeyboard(boolean simulate) {
              keyboardEventsEnabled = simulate;
    }

    @Override
    public boolean isSimulateKeyboard() {
        return keyboardEventsEnabled;
    }

    @Override
    public void setOmitHistoricEvents(boolean dontSendHistory) {
    }
          
    private TouchStatus getTouchStatus(int index) {
        while (touchStatus.size() <= index) {
            touchStatus.add(new TouchStatus());
        }
        return touchStatus.get(index);
    }

    private void scheduleEvent(TouchEvent.Type t, JSTouchEvent ev, boolean simulateMouse) {
        int nTouches = ev.getNumChangedTouches();
        for (int i = 0; i < nTouches; i++) {
            JSTouch touch = ev.getChangedTouch(i);
            TouchStatus s = getTouchStatus((int) touch.getIdentifier());
            int x = touch.getClientX();
            int y = touch.getClientY();

            x = canvas.getRelativePosX(x);
            y = canvas.getRelativePosY(y);
                           

            int dX = s.undefinedPos ? 0 : x - s.xPos;
            int dY = s.undefinedPos ? 0 : y - s.yPos;

            s.xPos = x;
            s.yPos = y;
            s.undefinedPos = false;

            long time = getInputTimeNanos();
            TouchEvent te = new TouchEvent(t, x, y, dX, dY);
            te.setTime(time);
            te.setPressure(touch.getForce());
            touchEvents.add(te);

            if (simulateMouse) {
                if (flipX) {
                    x = canvas.getWidth() - x;
                }
                if (flipY) {
                    y = canvas.getHeight() - y;
                }
                if (t == TouchEvent.Type.DOWN) {
                    MouseButtonEvent mev = new MouseButtonEvent(MouseInput.BUTTON_LEFT, true, x, y);
                    mev.setTime(time);
                    mouseButtonEvents.add(mev);
                } else if (t == TouchEvent.Type.UP) {
                    MouseButtonEvent mev=new MouseButtonEvent(MouseInput.BUTTON_LEFT, false, x, y);
                    mev.setTime(time);
                    mouseButtonEvents.add(mev);
                } else if (t == TouchEvent.Type.MOVE) {
                    MouseMotionEvent mev = new MouseMotionEvent(x, y, dX, dY, 0, 0);
                                        mev.setTime(time);

                    mouseMotionEvents.add(mev);
                }
            }

        }
        // ev.preventDefault();     
    }

    @Override
    public void handleEvent(Event evt) {

        if (evt.getType().equals("touchstart")) {
            scheduleEvent(TouchEvent.Type.DOWN, (JSTouchEvent) evt,this.isSimulateMouse());
        } else if (evt.getType().equals("touchcancel")) {
            scheduleEvent(TouchEvent.Type.UP, (JSTouchEvent) evt,this.isSimulateMouse());
        } else if( evt.getType().equals("touchend")) {
            scheduleEvent(TouchEvent.Type.UP, (JSTouchEvent) evt,this.isSimulateMouse());
        } else if (evt.getType().equals("touchmove")) {
            scheduleEvent(TouchEvent.Type.MOVE, (JSTouchEvent) evt,this.isSimulateMouse());
        }
    }

}
