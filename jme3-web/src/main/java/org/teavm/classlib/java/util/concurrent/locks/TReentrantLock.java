package org.teavm.classlib.java.util.concurrent.locks;

import java.util.LinkedList;
import java.util.Queue;

import org.teavm.common.Promise;

public class TReentrantLock {
    private volatile int lockCount = 0;
    private volatile Thread owner;
    private volatile boolean locked;

    public synchronized void lock() {
        // if (isLockOwner()) {
        //     lockCount++;
        // } else {             
            try {
                if (locked) {
                        wait();
                    
                }
                // else locked = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        // }
    }

    public synchronized void unlock() {
        // if (!isLockOwner()) {
        //     throw new IllegalStateException("Calling thread does not hold the lock.");
        // }
        lockCount--;
        if (lockCount == 0) {
            locked = false;
            // owner = null;
                notifyAll();
            
        }
    }

    private boolean isLockOwner() {
        return owner!=null&&owner  == Thread.currentThread();
    }

     

    public TCondition newCondition() {
        return new TCondition();
    }
}
