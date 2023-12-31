/*
 * Copyright (c) 2009-2023 jMonkeyEngine
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

package com.jme3.environment.baker;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.texture.FrameBuffer.FrameBufferTarget;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.TextureCubeMap;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.BufferUtils;

/**
 * Render the environment into a cubemap
 *
 * @author Riccardo Balbo
 */
public abstract class GenericEnvBaker implements EnvBaker {

    protected static Vector3f[] axisX = new Vector3f[6];
    protected static Vector3f[] axisY = new Vector3f[6];
    protected static Vector3f[] axisZ = new Vector3f[6];
    static {
        // PositiveX axis(left, up, direction)
        axisX[0] = Vector3f.UNIT_Z.mult(1.0F);
        axisY[0] = Vector3f.UNIT_Y.mult(-1.0F);
        axisZ[0] = Vector3f.UNIT_X.mult(1.0F);
        // NegativeX
        axisX[1] = Vector3f.UNIT_Z.mult(-1.0F);
        axisY[1] = Vector3f.UNIT_Y.mult(-1.0F);
        axisZ[1] = Vector3f.UNIT_X.mult(-1.0F);
        // PositiveY
        axisX[2] = Vector3f.UNIT_X.mult(-1.0F);
        axisY[2] = Vector3f.UNIT_Z.mult(1.0F);
        axisZ[2] = Vector3f.UNIT_Y.mult(1.0F);
        // NegativeY
        axisX[3] = Vector3f.UNIT_X.mult(-1.0F);
        axisY[3] = Vector3f.UNIT_Z.mult(-1.0F);
        axisZ[3] = Vector3f.UNIT_Y.mult(-1.0F);
        // PositiveZ
        axisX[4] = Vector3f.UNIT_X.mult(-1.0F);
        axisY[4] = Vector3f.UNIT_Y.mult(-1.0F);
        axisZ[4] = Vector3f.UNIT_Z;
        // NegativeZ
        axisX[5] = Vector3f.UNIT_X.mult(1.0F);
        axisY[5] = Vector3f.UNIT_Y.mult(-1.0F);
        axisZ[5] = Vector3f.UNIT_Z.mult(-1.0F);
    }

    protected TextureCubeMap env;
    protected Format depthFormat;

    protected final RenderManager renderManager;
    protected final AssetManager assetManager;
    protected final Camera cam;
    protected  boolean texturePulling=false;
    protected List<ByteArrayOutputStream> bos = new ArrayList<>();
    private static final Logger LOG=Logger.getLogger(GenericEnvBaker.class.getName());

 

    public GenericEnvBaker(RenderManager rm, AssetManager am, Format colorFormat, Format depthFormat, int env_size) {
        this.depthFormat = depthFormat;

        renderManager = rm;
        assetManager = am;

        cam = new Camera(128, 128);

        env = new TextureCubeMap(env_size, env_size, colorFormat);
        env.setMagFilter(MagFilter.Bilinear);
        env.setMinFilter(MinFilter.BilinearNoMipMaps);
        env.setWrap(WrapMode.EdgeClamp);
        env.getImage().setColorSpace(ColorSpace.Linear);
    }

    @Override
    public void setTexturePulling(boolean v) {
        texturePulling = v;
    }

    @Override
    public boolean isTexturePulling() {
        return texturePulling;
    }

    public TextureCubeMap getEnvMap() {
        return env;
    }

    Camera getCam(int id, int w, int h, Vector3f position, float frustumNear, float frustumFar) {
        cam.resize(w, h, false);
        cam.setLocation(position);
        cam.setFrustumPerspective(90.0F, 1F, frustumNear, frustumFar);
        cam.setRotation(new Quaternion().fromAxes(axisX[id], axisY[id], axisZ[id]));
        return cam;
    }

    @Override
    public void clean() {
    
    }

    @Override
    public void bakeEnvironment(Spatial scene, Vector3f position, float frustumNear, float frustumFar, Function<Geometry, Boolean> filter) {
        FrameBuffer envbakers[] = new FrameBuffer[6];
        for (int i = 0; i < 6; i++) {
            envbakers[i] = new FrameBuffer(env.getImage().getWidth(), env.getImage().getHeight(), 1);
            envbakers[i].setDepthTarget(FrameBufferTarget.newTarget(depthFormat));
            envbakers[i].setSrgb(false);
            envbakers[i].addColorTarget(FrameBufferTarget.newTarget(env).face(TextureCubeMap.Face.values()[i]));
        }

       

        if(isTexturePulling())startPulling();


        for (int i = 0; i < 6; i++) {
            FrameBuffer envbaker = envbakers[i];

            ViewPort viewPort = new ViewPort("EnvBaker", getCam(i, envbaker.getWidth(), envbaker.getHeight(), position, frustumNear, frustumFar));
            viewPort.setClearFlags(true, true, true);
            viewPort.setBackgroundColor(ColorRGBA.Pink);

            viewPort.setOutputFrameBuffer(envbaker);
            viewPort.clearScenes();
            viewPort.attachScene(scene);

            scene.updateLogicalState(0);
            scene.updateModelBound();
            scene.updateGeometricState();

            Function<Geometry, Boolean> ofilter = renderManager.getRenderFilter();

            renderManager.setRenderFilter(filter);
            renderManager.renderViewPort(viewPort, 0.16f);
            renderManager.setRenderFilter(ofilter);

            if (isTexturePulling()) pull(envbaker, env, i);
            
        }

        if (isTexturePulling()) endPulling(env);
        env.getImage().clearUpdateNeeded();
        for (int i = 0; i < 6; i++) {
            envbakers[i].dispose();
        }
    }
    

    /**
     * Starts pulling the data from the framebuffer into the texture
     */
    protected void startPulling() {
        bos.clear();
    }

    /**
     * Pulls the data from the framebuffer into the texture
     * Nb. mipmaps must be pulled sequentially on the same faceId
     * @param fb the framebuffer to pull from
     * @param env the texture to pull into
     * @param faceId id of face if cubemap or 0 otherwise
     * @return
     */
    protected ByteBuffer pull(FrameBuffer fb, Texture env, int faceId) {

        if (fb.getColorTarget().getFormat() != env.getImage().getFormat())
            throw new IllegalArgumentException("Format mismatch: " + fb.getColorTarget().getFormat() + "!=" + env.getImage().getFormat());

        ByteBuffer face = BufferUtils.createByteBuffer(fb.getWidth() * fb.getHeight() * (fb.getColorTarget().getFormat().getBitsPerPixel() / 8));
        renderManager.getRenderer().readFrameBufferWithFormat(fb, face, fb.getColorTarget().getFormat());
        face.rewind();

        while (bos.size() <= faceId) bos.add(null);
        ByteArrayOutputStream bo = bos.get(faceId);
        if (bo == null) bos.set(faceId, bo = new ByteArrayOutputStream());
        try {
            byte array[] = new byte[face.limit()];
            face.get(array);
            bo.write(array);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return face;
    }


    /**
     * Ends pulling the data into the texture
     * @param tx the texture to pull into
     */
    protected void endPulling(Texture tx) {
        for (int i = 0; i < bos.size(); i++) {
            ByteArrayOutputStream bo = bos.get(i);
            if (bo == null) {
                LOG.log(Level.SEVERE, "Missing face {0}. Pulling incomplete!", i);
                continue;
            }
            ByteBuffer faceMip = ByteBuffer.wrap(bo.toByteArray());
            tx.getImage().setData(i, faceMip);
        }
        bos.clear();
        tx.getImage().clearUpdateNeeded();
    }

}