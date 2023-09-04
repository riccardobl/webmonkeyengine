package com.jme3.bullet.gltf;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class ActivateRigidbodyControl extends AbstractControl {
    int updates = 0;

    @Override
    protected void controlUpdate(float tpf) {
        updates++;
        if (updates == 2) {
            RigidBodyControl ctr = spatial.getControl(RigidBodyControl.class);
            if (ctr != null)    ctr.setKinematic(false);            
            spatial.removeControl(this);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    
    }

    
}
