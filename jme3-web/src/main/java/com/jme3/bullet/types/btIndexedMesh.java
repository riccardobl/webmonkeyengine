package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btIndexedMesh extends JSObject {
     @JSMethod("get_m_numTriangles")
    public int getNumTriangles();

    @JSMethod("set_m_numTriangles")
    public void setNumTriangles(int numTriangles);

    @JSMethod("getTriangleIndexBase")
    public int getTriangleIndexBase();

    @JSMethod("setTriangleIndexBase")
    public void setTriangleIndexBase(int triangleIndexBase);

    @JSMethod("get_m_triangleIndexStride")
    public int getTriangleIndexStride();

    @JSMethod("set_m_triangleIndexStride")
    public void setTriangleIndexStride(int triangleIndexStride);

    @JSMethod("get_m_numVertices")
    public int getNumVertices();

    @JSMethod("set_m_numVertices")
    public void setNumVertices(int numVertices);

    @JSMethod("getVertexBase")
    public int getVertexBase();

    @JSMethod("setVertexBase")
    public void setVertexBase(int vertexBase);

    @JSMethod("get_m_vertexStride")
    public int getVertexStride();

    @JSMethod("set_m_vertexStride")
    public void setVertexStride(int vertexStride);
   

    
}
