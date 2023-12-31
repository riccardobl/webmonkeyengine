/*
 * Copyright (c) 2009-2021 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.bullet.collision.shapes;

import com.jme3.bullet.types.btCollisionShape;
import com.jme3.bullet.types.btDestructible;
import com.jme3.bullet.types.btDestructibleImpl;
import com.jme3.bullet.types.btUtils;
import com.jme3.bullet.types.btVector3;
import com.jme3.bullet.util.Converter;
import com.jme3.export.*;
import com.jme3.math.Vector3f;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * This Object holds information about a jbullet CollisionShape to be able to reuse
 * CollisionShapes (as suggested in bullet manuals)
 * TODO: add static methods to create shapes from nodes (like jbullet-jme constructor)
 * @author normenhansen
 */
public abstract class CollisionShape extends btDestructibleImpl implements Savable {
    private Logger logger = Logger.getLogger(CollisionShape.class.getName());
    public static enum ShapeType {
        CONVEX, CONCAVE, COMPOUND
    }
    /**
     * default margin for new shapes (in physics-space units, &gt;0,
     * default=0.04)
     */
    private static float defaultMargin = 0.1f;
    private btCollisionShape cShape;
    protected Vector3f scale = new Vector3f(1, 1, 1);
    /**
     * copy of collision margin (in physics-space units, &gt;0, default=0)
     */
    protected float margin = defaultMargin;

    protected CollisionShape() {

    }

    public abstract ShapeType getShapeType();


    /**
     * used internally, not safe
     * 
     * @param mass the desired mass for the body
     * @param vector storage for the result (not null, modified)
     */
    public void calculateLocalInertia(float mass,  btVector3 vector) {
        if (cShape == null) {
            return;
        }
        if (this instanceof MeshCollisionShape) {
            vector.setValue(0, 0, 0);
        } else {
            cShape.calculateLocalInertia(mass, vector);
        }
    }

    /**
     * used internally
     *
     * @return the pre-existing instance
     */
    public  btCollisionShape getCShape() {
        return cShape;
    }

    /**
     * used internally
     *
     * @param cShape the shape to use (alias created)
     */
    protected void setCShape(btCollisionShape cShape) {
        if (this.cShape != null) {            
            btUtils.destroy(this,this.cShape);            
        }
        this.cShape = cShape;
    }

    public void setScale(Vector3f scale) {
        btVector3 scaleTmp= btUtils.newVector3(this, 0, 0, 0);
        this.scale.set(scale);
        Converter.convert(scale,scaleTmp);
        cShape.setLocalScaling(scaleTmp);
        btUtils.destroy(this, scaleTmp);
    }

    public float getMargin() {
        return cShape.getMargin();
    }

    /**
     * Alter the default margin for new shapes.
     *
     * @param margin the desired margin distance (in physics-space units, &gt;0,
     * default=0.04)
     */
    public static void setDefaultMargin(float margin) {
        defaultMargin = margin;
    }

    /**
     * Read the default margin for new shapes.
     *
     * @return margin the default margin distance (in physics-space units,
     * &gt;0)
     */
    public static float getDefaultMargin() {
        return defaultMargin;
    }

    public void setMargin(float margin) {
        if (margin < defaultMargin) {
            logger.warning("Setting margin < defaultMargin");
        }
        cShape.setMargin(margin);
        this.margin = margin;
    }

    public Vector3f getScale() {
        return scale;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(scale, "scale", new Vector3f(1, 1, 1));
        capsule.write(getMargin(), "margin", 0.0f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        this.scale = (Vector3f) capsule.readSavable("scale", new Vector3f(1, 1, 1));
        this.margin = capsule.readFloat("margin", 0.0f);
    }


}
