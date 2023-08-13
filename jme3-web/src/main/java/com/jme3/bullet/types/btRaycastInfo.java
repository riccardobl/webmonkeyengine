package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btRaycastInfo extends JSObject {
    @JSMethod("get_m_contactNormalWS")
    public btVector3 getContactNormalWS();

    @JSMethod("get_m_contactPointWS")
    public btVector3 getContactPointWS();
    

    @JSMethod("get_m_groundObject")
    public btCollisionObject getGroundObject();

}
