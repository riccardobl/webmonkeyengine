package com.jme3.bullet.types;

public interface btConcaveShape extends btCollisionShape {
    
    void processAllTriangles(btTriangleCallback callback, btVector3 aabbMin, btVector3 aabbMax);
    
}
