package com.jme3.bullet.types;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public interface btDynamicsWorld extends JSObject {
    public void setGravity(btVector3 gravity);

 
    
     @JSBody(params = { "callback" }, script = "return Ammo.addFunction((cp, colObj0, colObj1) => { \n" + 
             "        colObj0 = Ammo.wrapPointer(colObj0, Ammo.btCollisionObject); \n" + 
             "        colObj1 = Ammo.wrapPointer(colObj1, Ammo.btCollisionObject); cp = \n" + 
             "        Ammo.wrapPointer(cp,Ammo.btManifoldPoint);\n" + 
             "        callback(cp, colObj0, colObj1)\n" + 
             "    });")
     public void setContactProcessedCallback(btContactProcessedCallback callback);

}
