package org.teavm.classlib.java.util.concurrent.locks;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class TCondition {
 
    

    private volatile int lockCount = 0;
    private volatile Thread owner;
    private volatile boolean locked;

    public synchronized void await() throws InterruptedException {
        await(10000, TimeUnit.MILLISECONDS);
    }

    public synchronized void await(long timeout, TimeUnit unit) throws InterruptedException {
        // if (isLockOwner()) {
        //     lockCount++;
        // } else {
            try {
                if (locked) {
                    long millis = unit.toMillis(timeout);

                        wait(millis);
                    
                } else locked = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        // }
    }

    public synchronized void signalAll() {
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

    // private boolean isLockOwner() {
    //     return owner != null && owner == Thread.currentThread();
    // }
 
}
