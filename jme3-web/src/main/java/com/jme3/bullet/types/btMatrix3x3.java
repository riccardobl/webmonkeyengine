package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btMatrix3x3 extends JSObject{
    // void setValue (const btScalar &xx, const btScalar &xy, const btScalar
    // &xz, const btScalar &yx, const btScalar &yy, const btScalar &yz, const
    // btScalar &zx, const btScalar &zy, const btScalar &zz)
    public void setValue(float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy, float zz);

    public btVector3 getRow(int i);
}
