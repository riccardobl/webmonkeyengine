package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btAllHitsRayResultCallback extends JSObject {

    @JSMethod("get_m_hitFractions")
    public btScalarArray getHitFractions();

    @JSMethod("get_m_hitPointWorld")
    public btVector3Array getHitPointWorld();

    @JSMethod("get_m_hitNormalWorld")
    public btVector3Array getHitNormalWorld();

    @JSMethod("get_m_collisionObjects")
    public btConstCollisionObjectArray getCollisionObjects();

}
