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
package com.jme3.bullet.joints;

import com.jme3.bullet.types.btHingeConstraint;
import com.jme3.bullet.types.btRigidBody;
import com.jme3.bullet.types.btTypedConstraint;
import com.jme3.bullet.types.btUtils;
import com.jme3.bullet.types.btVector3;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.Converter;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import java.io.IOException;

/**
 * <i>From bullet manual:</i><br>
 * Hinge constraint, or revolute joint restricts two additional angular degrees of freedom,
 * so the body can only rotate around one axis, the hinge axis.
 * This can be useful to represent doors or wheels rotating around one axis.
 * The user can specify limits and motor for the hinge.
 * @author normenhansen
 */
public class HingeJoint extends PhysicsJoint {

    protected Vector3f axisA;
    protected Vector3f axisB;
    protected boolean angularOnly = false;
    protected float biasFactor = 0.3f;
    protected float relaxationFactor = 1.0f;
    protected float limitSoftness = 0.9f;

    protected HingeJoint() {
    }

    /**
     * Creates a new HingeJoint
     *
     * @param nodeA the body for the A end (not null, alias created)
     * @param nodeB the body for the B end (not null, alias created)
     * @param pivotA local translation of the joint connection point in node A
     * @param pivotB local translation of the joint connection point in node B
     * @param axisA the joint axis in A's local coordinates (unit vector,
     * alias created)
     * @param axisB the joint axis in B's local coordinates (unit vector,
     * alias created)
     */
    public HingeJoint(PhysicsRigidBody nodeA, PhysicsRigidBody nodeB, Vector3f pivotA, Vector3f pivotB, Vector3f axisA, Vector3f axisB) {
        super(nodeA, nodeB, pivotA, pivotB);
        this.axisA = axisA;
        this.axisB = axisB;
        createJoint();
    }

    /**
     * Enables the motor.
     * @param enable if true, motor is enabled.
     * @param targetVelocity the target velocity of the rotation.
     * @param maxMotorImpulse the max force applied to the hinge to rotate it.
     */
    public void enableMotor(boolean enable, float targetVelocity, float maxMotorImpulse) {
        ((btHingeConstraint) getConstraint()).enableAngularMotor(enable, targetVelocity, maxMotorImpulse);
    }

    /**
     * Sets the limits of this joint.
     * @param low the low limit in radians.
     * @param high the high limit in radians.
     */
    public void setLimit(float low, float high) {
        ((btHingeConstraint) getConstraint()).setLimit(low, high,0.9f,0.3f,1.0f);
    }

    /**
     * Sets the limits of this joint.
     * If you're above the softness, velocities that would shoot through the actual limit are slowed down. The bias be in the range of 0.2 - 0.5.
     * @param low the low limit in radians.
     * @param high the high limit in radians.
     * @param _softness the factor at which the velocity error correction starts operating,i.e a softness of 0.9 means that the vel. corr starts at 90% of the limit range.
     * @param _biasFactor the magnitude of the position correction. It tells you how strictly the position error (drift ) is corrected.
     * @param _relaxationFactor the rate at which velocity errors are corrected. This can be seen as the strength of the limits. A low value will make the limits more spongy.
     */
    public void setLimit(float low, float high, float _softness, float _biasFactor, float _relaxationFactor) {
        biasFactor = _biasFactor;
        relaxationFactor = _relaxationFactor;
        limitSoftness = _softness;
        ((btHingeConstraint) getConstraint()).setLimit(low, high, _softness, _biasFactor, _relaxationFactor);
    }

    public float getUpperLimit(){
        return ((btHingeConstraint) getConstraint()).getUpperLimit();
    }

    public float getLowerLimit(){
        return ((btHingeConstraint) getConstraint()).getLowerLimit();
    }

    public void setAngularOnly(boolean angularOnly) {
        this.angularOnly = angularOnly;
        ((btHingeConstraint) getConstraint()).setAngularOnly(angularOnly);
    }

    public float getHingeAngle() {
        return ((btHingeConstraint) getConstraint()).getHingeAngle();
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(axisA, "axisA", new Vector3f());
        capsule.write(axisB, "axisB", new Vector3f());

        capsule.write(angularOnly, "angularOnly", false);

        capsule.write(((btHingeConstraint) getConstraint()).getLowerLimit(), "lowerLimit", 1e30f);
        capsule.write(((btHingeConstraint) getConstraint()).getUpperLimit(), "upperLimit", -1e30f);

        capsule.write(biasFactor, "biasFactor", 0.3f);
        capsule.write(relaxationFactor, "relaxationFactor", 1f);
        capsule.write(limitSoftness, "limitSoftness", 0.9f);

        capsule.write(((btHingeConstraint) getConstraint()).getEnableAngularMotor(), "enableAngularMotor", false);
        capsule.write(((btHingeConstraint) getConstraint()).getMotorTargetVelocity(), "targetVelocity", 0.0f);
        capsule.write(((btHingeConstraint) getConstraint()).getMaxMotorImpulse(), "maxMotorImpulse", 0.0f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        this.axisA = (Vector3f) capsule.readSavable("axisA", new Vector3f());
        this.axisB = (Vector3f) capsule.readSavable("axisB", new Vector3f());

        this.angularOnly = capsule.readBoolean("angularOnly", false);
        float lowerLimit = capsule.readFloat("lowerLimit", 1e30f);
        float upperLimit = capsule.readFloat("upperLimit", -1e30f);

        this.biasFactor = capsule.readFloat("biasFactor", 0.3f);
        this.relaxationFactor = capsule.readFloat("relaxationFactor", 1f);
        this.limitSoftness = capsule.readFloat("limitSoftness", 0.9f);

        boolean enableAngularMotor=capsule.readBoolean("enableAngularMotor", false);
        float targetVelocity=capsule.readFloat("targetVelocity", 0.0f);
        float maxMotorImpulse=capsule.readFloat("maxMotorImpulse", 0.0f);

        createJoint();
        enableMotor(enableAngularMotor, targetVelocity, maxMotorImpulse);
        ((btHingeConstraint) getConstraint()).setLimit(lowerLimit, upperLimit, limitSoftness, biasFactor, relaxationFactor);
    }

    private void createJoint() {
        btVector3 pA = btUtils.newVector3(this, 0, 0, 0);
        btVector3 pB = btUtils.newVector3(this, 0, 0, 0);
        btVector3 aA = btUtils.newVector3(this, 0, 0, 0);
        btVector3 aB = btUtils.newVector3(this, 0, 0, 0);

        Converter.convert(pivotA, pA);
        Converter.convert(pivotB, pB);
        Converter.convert(axisA, aA);
        Converter.convert(axisB, aB);
        

        btHingeConstraint constraint = btUtils.createHingeConstraint(this,(btRigidBody)nodeA.getBtObject(), (btRigidBody)nodeB.getBtObject(),
                pA, pB,
                aA, aB,false);
        ((btHingeConstraint) constraint).setAngularOnly(angularOnly);
        setConstraint(constraint);

        btUtils.destroy(this,pA);
        btUtils.destroy(this,pB);
        btUtils.destroy(this,aA);
        btUtils.destroy(this,aB);
    }
}
