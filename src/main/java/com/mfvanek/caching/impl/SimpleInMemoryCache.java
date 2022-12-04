/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cacheable;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The simplest thread unsafe implementation of in-memory cache.
 *
 * @param <K> key type
 * @param <V> value type, should be {@link Cacheable}
 */
public final class SimpleInMemoryCache<K, V extends Cacheable<K>> extends AbstractMapCache<K, V> {

    public SimpleInMemoryCache(final Class<V> type, final int maxCacheSize) {
        super(type, maxCacheSize, new LinkedHashMap<>(maxCacheSize) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
                return size() > maxCacheSize;
            }
        });
    }

    @Override
    public List<Map.Entry<K, V>> put(final K key, final V value) {
        getInnerMap().put(key, value);
        // In this case we always return an empty list because we don't control eviction from the cache
        return List.of();
    }

    @Override
    public Map.Entry<Integer, V> innerRemove(final K key) {
        return new AbstractMap.SimpleEntry<>(INVALID_FREQUENCY, remove(key));
    }

    @Override
    public int frequencyOf(final K key) {
        return INVALID_FREQUENCY;
    }

    @Override
    public int getLowestFrequency() {
        return INVALID_FREQUENCY;
    }
}
