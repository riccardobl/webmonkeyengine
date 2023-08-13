package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

import com.jme3.math.Vector3f;

public interface btClosestConvexResultCallback extends JSObject {

 @JSMethod("get_m_convexFromWorld")
    public btVector3 getConvexFromWorld();
    
    @JSMethod("set_m_convexFromWorld")
    public void setConvexFromWorld(btVector3 value);
    
    @JSMethod("get_m_convexToWorld")
    public Vector3f getConvexToWorld();
    
    @JSMethod("set_m_convexToWorld")
    public void setConvexToWorld(btVector3 value);
    
    @JSMethod("get_m_hitNormalWorld")
    public btVector3 getHitNormalWorld();
    
    @JSMethod("set_m_hitNormalWorld")
    public void setHitNormalWorld(btVector3 value);
    
    @JSMethod("get_m_hitPointWorld")
    public btVector3 getHitPointWorld();
    
    @JSMethod("set_m_hitPointWorld")
    public void setHitPointWorld(btVector3 value);
    
    @JSMethod("get_m_hitCollisionObject")
    public btCollisionObject getHitCollisionObject();
    
    @JSMethod("set_m_hitCollisionObject")
    public void setHitCollisionObject(btCollisionObject value);
}
