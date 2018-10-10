package org.iota.jota.stream;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.iota.jota.stream.buffer.BoundedHashSet;

public class Buffer<T> {
    
    private static final int DEFAULT_SIZE = 100;
    private static final int PRODUCE_DELAY_MS = 200;
    
    private BoundedHashSet<T> set;

    private Producer<T> producer;

    private ScheduledExecutorService thread;
    
    private Object threadSync = new Object();
    
    /**
     * Creates a new buffer with this producer, with this max size
     * @param size the size this buffer should max generate in advance
     * @param producer The producer that makes our objects
     * @param start if we should start immediately
     */
    public Buffer(int size, Producer<T> producer, boolean start) {
        set = new BoundedHashSet<>(size);
        
        this.producer = producer;
        if (start) start();
    }
    
    /**
     * Creates a new buffer with this producer
     * @param producer
     * @param start if we should start immediately
     */
    public Buffer(Producer<T> producer, boolean start) {
        this(DEFAULT_SIZE, producer, start);
    }
    
    /**
     * Creates a new buffer with this producer, and starts it
     * @param producer
     */
    public Buffer(Producer<T> producer) {
        this(DEFAULT_SIZE, producer, true);
    }
    
    public void start() {
        if (null != thread) {
            //We already started!
            return;
        }
        
        thread = Executors.newSingleThreadScheduledExecutor();
        thread.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (threadSync) {
                    if (set.isFull()) return;
                    
                    set.add(producer.next());
                }
            }
        }, 0, PRODUCE_DELAY_MS, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Stops the running thread, if there is one
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        if (null == thread) return;
        
        thread.awaitTermination(PRODUCE_DELAY_MS*2, TimeUnit.MILLISECONDS);
        thread = null;
    }
    
    public int getMaxSize() {
        return set.getMaxSize();
    }
    
    public boolean hasNext() {
        return !set.isEmpty();
    }
    
    public T next() {
        synchronized (threadSync) {
            Iterator<T> iter = set.iterator();
            return iter.next();
        }
    }
}
