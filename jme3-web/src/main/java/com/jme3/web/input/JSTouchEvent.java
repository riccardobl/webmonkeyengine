package com.jme3.web.input;

import org.teavm.jso.JSBody;
import org.teavm.jso.dom.events.Event;

public interface JSTouchEvent extends Event {
    
    @JSBody( script = "return this.touches.length;")
    public int getNumTouches();

    @JSBody(params = {  "index" }, script = "return this.touches[index];")
    public JSTouch getTouch(int index);

    @JSBody( script = "return this.changedTouches.length;")
    public int getNumChangedTouches();

    @JSBody(params = {  "index" }, script = "return this.changedTouches[index];")
    public JSTouch getChangedTouch(int index);

}
