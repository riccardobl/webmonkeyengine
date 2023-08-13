package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TExecutors {
    public static TExecutorService newFixedThreadPool(int nThreads, TThreadFactory threadFactory) {
        return new TExecutorService();
    }

}
