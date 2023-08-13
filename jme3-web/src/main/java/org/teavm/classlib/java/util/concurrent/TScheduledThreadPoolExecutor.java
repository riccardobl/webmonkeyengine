package org.teavm.classlib.java.util.concurrent;

 import java.lang.Override;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
 import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.teavm.classlib.java.lang.TRunnable;
import org.teavm.classlib.java.util.concurrent.TExecutor;
import org.teavm.jso.browser.Window;

public class TScheduledThreadPoolExecutor implements Executor {

    // private final Thread pool[];
    // private final LinkedList<Runnable> queues[];
    private  volatile boolean running = true;
    // private final AtomicInteger queueIndex = new AtomicInteger(0);

    public TScheduledThreadPoolExecutor(int poolSize) {
        // pool = new Thread[poolSize];
        // queues = new LinkedList[poolSize];
        // for (int i = 0; i < poolSize; i++) {
        //     queues[i] = new LinkedList<Runnable>();
        //     LinkedList<Runnable> queue = queues[i];
        //     pool[i] = new Thread(new Runnable() {
        //         @Override
        //         public void run() {
        //             while (running) {
        //                 Runnable r;
        //                 synchronized (queue) {
        //                     while (queue.isEmpty()) {
        //                         try {
        //                             queue.wait();
        //                         } catch (InterruptedException e) {
        //                             e.printStackTrace();
        //                         }
        //                     }
        //                     r = queue.removeFirst();
        //                 }
        //                 r.run();
        //             }
        //         }
        //     });
        //     pool[i].start();
        // }

    }
    

    public <V> TFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        long ms = unit.toMillis(delay);
        TFutureTask<V> futureTask = new TFutureTask<V>();
        Window.setTimeout(() -> {
            
           try {
                V res = callable.call();
                futureTask.setResult(res);
            
            } catch (Exception e) {
                        e.printStackTrace();

            futureTask.setException(e);
        }
        },ms);
        return futureTask;


    }
    @Override
    public void execute(Runnable command) {
        submit(command);
    }

    public <T> TFuture<T> submit(Runnable task,T result) {
        TFuture<T> res = schedule(() -> {
            try{
                task.run();
                afterExecute(task, null);
            }catch(Exception e){
                e.printStackTrace();
                afterExecute(task, e);

            }
            return result;
        }, 0, TimeUnit.MILLISECONDS);
        return res;
    
    }
    
     public <T> TFuture<T> submit(Runnable task) {
        return submit(task, null);
    }

    public <T> TFuture<T> submit(Callable<T> task) {
        return schedule(task, 0, TimeUnit.MILLISECONDS);
    }

    protected void afterExecute(Runnable r, Throwable t) {
    }

    public  List<Runnable> shutdownNow() {
        running = false;
        return new ArrayList<Runnable>();
    }
    
    public void shutdown() {
        running = false;
    }
}
