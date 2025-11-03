package com.speechify;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Use the provided com.speechify.LRUCacheProviderTest in `src/test/java/LruCacheTest.java` to validate your
 * implementation.
 *
 * You may:
 *  - Read online API references for Java standard library or JVM collections.
 * You must not:
 *  - Read guides about how to code an LRU cache.
 */

public class LRUCacheProvider {
    public static <T> LRUCache<T> createLRUCache(CacheLimits options) {
        return new LRUCacheImpl<>(options.getMaxItemsCount());
    }

    private static class LRUCacheImpl<T> implements LRUCache<T> {
        private final LinkedHashMap<String, T> map;
        private final int maxSize;

        public LRUCacheImpl(int maxSize) {
            this.maxSize = maxSize;
            this.map   = new LinkedHashMap<String, T>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, T> eldest) {
                    return size() > LRUCacheImpl.this.maxSize;
                }
            };
        }

        @Override
        public T get(String key) {
            return map.get(key);
        }

        @Override
        public void set(String key, T value) {
            map.put(key, value);
        }
    }
}
