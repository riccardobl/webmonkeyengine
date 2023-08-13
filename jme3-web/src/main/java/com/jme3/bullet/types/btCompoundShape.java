package com.jme3.bullet.types;

public interface btCompoundShape extends btCollisionShape {
    
    public void removeChildShape(btCollisionShape shape);
    public void addChildShape(btTransform localTransform, btCollisionShape shape);
}
