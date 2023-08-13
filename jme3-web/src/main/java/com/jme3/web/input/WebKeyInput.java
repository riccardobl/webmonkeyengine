package com.jme3.web.input;

import java.util.ArrayList;
import java.util.List;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.html.HTMLDocument;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.web.context.WebCanvasElement;

public class WebKeyInput implements KeyInput, EventListener {
    private boolean initialized = false;
    private RawInputListener listener;

    private final List<KeyInputEvent> keyEvents = new ArrayList<>();
        private WebCanvasElement canvas;

       public WebKeyInput(WebCanvasElement canvas) {
        this.canvas = canvas;
    }

    @Override
    public void initialize() {
        Window win = Window.current();
        HTMLDocument doc = win.getDocument();
        doc.addEventListener("keydown", this, false);
        doc.addEventListener("keyup", this, false);     
        initialized = true;
    }

    @Override
    public void update() {
        for (KeyInputEvent evt : keyEvents) {
            if (listener != null) {
                listener.onKeyEvent(evt);
            }
        }
        keyEvents.clear();
    }

    @Override
    public void destroy() {
        Window win = Window.current();
        HTMLDocument doc = win.getDocument();
        doc.removeEventListener("keydown", this);
        doc.removeEventListener("keyup", this);
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
    public void handleEvent(Event evt) {
        long time = getInputTimeNanos();
        KeyboardEvent ev = (KeyboardEvent) evt;
        int keyCode = ev.getKeyCode();
        int keyCharCode = ev.getCharCode();
        
        int jmeKeyCode=KeyMapper.jsKeyCodeToJme(keyCode);
        char jmeKeyChar = (char) keyCharCode;
        boolean isPressed = ev.getType().equals("keydown");
        
        KeyInputEvent jmeEvent=new KeyInputEvent(jmeKeyCode, jmeKeyChar, isPressed, false);
        jmeEvent.setTime(time);
        keyEvents.add(jmeEvent);
    }

    @Override
    public String getKeyName(int key) {
        return KeyMapper.getKeyNameJme(key);
    }
    
}
