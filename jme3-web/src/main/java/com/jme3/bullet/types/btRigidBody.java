package com.jme3.bullet.types;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

public interface btRigidBody extends btCollisionObject {

    boolean isInWorld();

    btTransform getCenterOfMassTransform();

    void setCenterOfMassTransform(btTransform v);

    void setMassProps(float mass, btVector3 inertia);

    btVector3 getGravity();

    void setGravity(btVector3 acceleration);

    void setDamping(float lin_damping, float ang_damping);

    btVector3 getLinearVelocity();

    void setLinearVelocity(btVector3 lin_vel);

    btVector3 getAngularVelocity();

    void setAngularVelocity(btVector3 ang_vel);

    void setSleepingThresholds(float linear, float angular);

    void applyCentralForce(btVector3 force);

    void applyTorque(btVector3 torque);

    void applyForce(btVector3 force, btVector3 rel_pos);

    void applyCentralImpulse(btVector3 impulse);

    void applyTorqueImpulse(btVector3 torque);

    void applyImpulse(btVector3 impulse, btVector3 rel_pos);

    void clearForces();

    btVector3 getAngularFactor();

    void setAngularFactor(btVector3 angFac);

    void setLinearFactor(btVector3 v);
}


