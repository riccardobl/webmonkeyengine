package org.teavm.classlib.java.util.concurrent;

public interface TThreadFactory {
    public default Thread newThread(final Runnable task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        return t;
    }
}
