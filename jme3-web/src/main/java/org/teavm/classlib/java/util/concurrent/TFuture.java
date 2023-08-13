package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
public interface TFuture<V>  {
    
    /**
     * Attempts to cancel execution of this task. This attempt will fail if the
     * task has already completed, has already been cancelled, or could not be
     * cancelled for some other reason. If successful, and this task has not
     * started when {@code cancel} is called, this task should never run. If the
     * task has already started, then the {@code mayInterruptIfRunning}
     * parameter determines whether the thread executing this task should be
     * interrupted in an attempt to stop the task.
     *
     * <p>
     * After this method returns, subsequent calls to {@link #isDone} will
     * always return {@code true}. Subsequent calls to {@link #isCancelled} will
     * always return {@code true} if this method returned {@code true}.
     *
     * @param mayInterruptIfRunning
     *            {@code true} if the thread executing this task should be
     *            interrupted; otherwise, in-progress tasks are allowed to
     *            complete
     * @return {@code false} if the task could not be cancelled, typically
     *         because it has already completed normally; {@code true} otherwise
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * Returns {@code true} if this task was cancelled before it completed
     * normally.
     *
     * @return {@code true} if this task was cancelled before it completed
     */
    boolean isCancelled();

    /**
     * Returns {@code true} if this task completed.
     *
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.
     *
     * @return {@code true} if this task completed
     */
    boolean isDone();

    /**
     * Waits if necessary for the computation to complete, and then retrieves
     * its result.
     *
     * @return the computed result
     * @throws TCancellationException
     *             if the computation was cancelled
     * @throws TExecutionException
     *             if the computation threw an exception
     * @throws InterruptedException
     *             if the current thread was interrupted while waiting
     */
    V get() throws InterruptedException, TExecutionException, TCancellationException;

    /**
     * Waits if necessary for at most the given time for the computation to
     * complete, and then retrieves its result, if available.
     *
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return the computed result
     * @throws TCancellationException
     *             if the computation was cancelled
     * @throws TExecutionException
     *             if the computation threw an exception
     * @throws InterruptedException
     *             if the current thread was interrupted while waiting
     * @throws TimeoutException
     *             if the wait timed out
     */
    V get(long timeout, TimeUnit unit) throws InterruptedException, TExecutionException, TCancellationException, TimeoutException;
}
