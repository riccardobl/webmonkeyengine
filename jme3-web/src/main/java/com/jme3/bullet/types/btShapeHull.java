package com.jme3.bullet.types;

import org.teavm.jso.JSObject;

public interface btShapeHull extends JSObject{
    
    public boolean buildHull(float margin);

    public int numVertices();

    public default int  numTriangles(){
        return numIndices()/3;
    }
    
    public int numIndices();

    public btVector3 vertexAt(int i);

    public int indexAt(int i);

    public btVector3 getVertexPointer();
    
    
}
