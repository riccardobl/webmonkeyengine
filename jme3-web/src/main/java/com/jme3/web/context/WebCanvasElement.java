package com.jme3.web.context;

import org.teavm.jso.JSBody;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.TextRectangle;

public abstract class WebCanvasElement implements HTMLCanvasElement {
    public abstract void requestPointerLock(PointerLockOptions options);

    @JSBody(params = { }, script = "document.exitPointerLock();")
    public abstract void exitPointerLock();

    @JSBody(params = { }, script = "return !!document.pointerLockElement;")
    public abstract boolean isPointerLocked();


    @JSBody(params = {"deltaValue","deltaMode"}, script = "return window.jme.getPixelDeltaScroll(deltaValue,deltaMode);")
    public abstract float getPixelDeltaScrollY(float deltaValue, int deltaMode);


    public int getRelativePosX(int x) {
        TextRectangle r = this.getBoundingClientRect();
        return x - r.getLeft();
    }
    
    public int getRelativePosY(int y) {
        TextRectangle r = this.getBoundingClientRect();
        return y-r.getTop();
    }

    public abstract void requestFullscreen();


    @JSBody(params = { }, script = "return !!document.fullscreenElement;")
    public abstract boolean isFullscreen();

    @JSBody(params = { }, script = "document.exitFullscreen();")
    public abstract void exitFullscreen();

    @JSBody(params = { }, script = "return window.jme.canvasFitParent(this);")
    public abstract void canvasFitParent();
    

}
