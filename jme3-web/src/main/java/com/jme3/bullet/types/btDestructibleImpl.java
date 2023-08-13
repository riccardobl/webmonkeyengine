package com.jme3.bullet.types;

import java.util.ArrayList;
import java.util.List;

public class btDestructibleImpl implements btDestructible {
    
    private final ArrayList<Object> objects = new ArrayList<Object>();
    private boolean destroyed = false;
    public  void destroy() {
        for (Object obj : objects) {
            btUtils.destroy(this,obj);
        }
        objects.clear();
    }

    public final void gc(Object obj) {
        for (Object o : objects) {
            if (o == obj) return;
        }
        objects.add(obj);
    }
    
    public final void ungc(Object obj) {
        for(int i=0;i<objects.size();i++) {
            if (objects.get(i) == obj) {
                objects.remove(i);
                return;
            }
        }
      
    }

    @Override
    public final void finalize() {
        destroy();
    }

    @Override
    public final void markDestroyed() {
        destroyed = true;
    }

    @Override
    public final boolean isDestroyed() {
        return destroyed;
    }
    
}
