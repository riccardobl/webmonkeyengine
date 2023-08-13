package com.jme3.bullet.types;

public interface btPairCachingGhostObject extends btCollisionObject{
    
    
    public int getNumOverlappingObjects();

    public btCollisionObject getOverlappingObject(int index);
    

}
