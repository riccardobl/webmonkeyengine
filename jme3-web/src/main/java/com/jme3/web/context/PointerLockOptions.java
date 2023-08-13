package com.jme3.web.context;

 
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public abstract class  PointerLockOptions implements JSObject {
    

    
    @JSProperty("unadjustedMovement")
    public abstract void setUnadjustedMovement(boolean unadjustedMovement);



    @JSBody(params = {  }, script = "return {}")
    public static native PointerLockOptions create();
}
