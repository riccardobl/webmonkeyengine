package com.jme3.bullet.types;

public interface btDestructible {
    public void destroy();

    public void gc(Object obj);
    public void ungc(Object obj);

    public void markDestroyed();
    public boolean isDestroyed();
}
