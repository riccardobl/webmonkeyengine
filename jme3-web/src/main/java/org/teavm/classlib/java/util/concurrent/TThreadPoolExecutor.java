package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TThreadPoolExecutor extends TExecutorService{
    public TThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
            TThreadFactory threadFactory) {
        super();
    }
    
}
