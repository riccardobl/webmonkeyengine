package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ExecutorService;

public class TExecutorService extends TScheduledThreadPoolExecutor{

    public TExecutorService() {
        super(1);
        
    }

    public static TExecutorService newFixedThreadPool(int nThreads, TThreadFactory threadFactory) {
        return new TExecutorService();
    }

}
