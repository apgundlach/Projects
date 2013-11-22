/**
 * Name:  		Adelle Paulette Rivera
 * Class:  		CS212
 * Assignment:  Project 3
 **/

import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * Work queue implementation adapted from
 * <a href="http://www.ibm.com/developerworks/library/j-jtp0730.html">
 * http://www.ibm.com/developerworks/library/j-jtp0730.html</a>.
 * Modifications include removing raw types, adding log4j messages, and
 * adding shutdown capability.
 *
 * @author Sophie Engle
 */

public class WorkQueue {
	/** Thread pool. **/
    private final PoolWorker[] workers;

    /** Work queue. Using a {@link LinkedList} as a queue.**/
    private final LinkedList<Runnable> queue;

    /** Shutdown flag. Example of using a <code>volatile</code> variable. **/
    private volatile boolean shutdown;

    /**
     * Class-specific logger. Allows debug messages for this class to be
     * disabled by adding <code>WorkQueue.log.setLevel(Level.OFF)</code>
     * in your code.
     */
	private static Logger log = Logger.getLogger(WorkQueue.class);

	/**
	 * Creates a thread pool and work queue and starts the worker threads.
	 *
	 * @param threads number of worker threads (must be at least 1)
	 */
    public WorkQueue(int threads) {
    		if(threads < 1) {
    			throw new IllegalArgumentException("Only " + threads +
    					" worker threads specified. Must have at least" +
    					" one worker thread.");
    		}

        this.queue   = new LinkedList<Runnable>();
        this.workers = new PoolWorker[threads];

        shutdown = false;

        for(int i = 0; i < threads; i++) {
            workers[i] = new PoolWorker();
            workers[i].start();
        }

        log.debug("WorkQueue initialized with " + threads + " threads.");
    }

    /**
     * Adds work to the work queue to be executed by the next available
     * worker thread. Note that this method is not synchronized! Do you
     * understand why?
     *
     * @param r work to execute
     */
    public void execute(Runnable r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notify();
        }
    }

    /**
     * Shuts down the work queue after allowing any currently executing
     * threads to finish first. Might leave unfinished work in queue.
     */
    public void shutdown() {
        log.debug("Attempting shutdown of all threads.");
        shutdown = true;

        synchronized(queue) {
        		queue.notifyAll();
        }
    }

    /**
     * Worker thread class. Each thread looks for work in the work queue
     * and executes the work if found. Otherwise, the thread will wait
     * until notified that there is now work in the queue.
     */
    private class PoolWorker extends Thread {
    		@Override
        public void run() {
            Runnable r = null;

            while(true) {
                synchronized(queue) {
                    while(queue.isEmpty() && !shutdown) {
                        try {
                            log.debug("Waiting for work...");
                            queue.wait();
                        }
                        catch (InterruptedException ex) {
                            log.debug("Interrupted while waiting for work.");
                        }
                    }

                    if(shutdown) {
                        log.debug("Shutdown detected.");
                        break;
                    }
                    else {
                        assert !queue.isEmpty() : "Work queue empty.";
                        r = queue.removeFirst();
                    }
                }

                try {
                    log.debug("Found work!");
                    assert r != null : "Runnable object null.";
                    r.run();
                }
                catch (RuntimeException ex) {
                    log.error("Encountered exception running work.", ex);
                }
            }

            log.debug("Thread terminating.");
        }
    }
}