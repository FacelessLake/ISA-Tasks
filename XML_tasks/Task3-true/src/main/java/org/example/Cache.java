package org.example;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Cache<K, V> {
    private final Trigger<K> getTrigger = new Trigger<>();
    private final Trigger<K> putTrigger = new Trigger<>();
    private final Timer<K> cacheTimer = new Timer<>();
    private final Function<K, V> producer;
    private final ReferenceQueue<V> referenceQueue = new ReferenceQueue<>();
    private final Map<K, CacheReference> cache = Collections.synchronizedMap(new HashMap<K, CacheReference>());

    private class CacheReference extends PhantomReference<V> {
        public final K key;

        public CacheReference(K key, V referent) {
            super(referent, Cache.this.referenceQueue);
            this.key = key;
        }
    }

    @SuppressWarnings("unchecked")
    private class ReferenceCleaner implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    CacheReference reference = (CacheReference) referenceQueue.remove();
                    remove(reference.key);
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    public Cache(Function<K, V> producer) {
        this.producer = producer;
        Thread cleaner = new Thread(new ReferenceCleaner());
        cleaner.setDaemon(true);
        cleaner.start();
    }

    protected void createValue(K key) {
        putTrigger.count(key);
        cacheTimer.start(key);
        V value = producer.apply(key);
        cache.put(key, new CacheReference(key, value));
    }

    public void getValue(K key) {
        getTrigger.count(key);
        CacheReference reference = cache.get(key);
        if (reference != null) {
            V value = reference.get();
            if (value != null) {
                return;
            }
        }
        createValue(key);
    }

    protected void remove(K key) {
        cacheTimer.stop(key);
        cache.remove(key);
    }

    public double getProbability(K key) {
        double total = getTrigger.get(key);
        double part = putTrigger.get(key);
        return total == 0 ? 0 : 1 - (part / total);
    }

    public long getLifetime(K key) {
        return cacheTimer.getTotalTime(key) / putTrigger.get(key);
    }

}
