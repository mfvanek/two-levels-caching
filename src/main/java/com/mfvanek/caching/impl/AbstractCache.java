/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.CacheExtended;
import com.mfvanek.caching.interfaces.Cacheable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract base class for implementing cache.
 *
 * @param <K> key type
 * @param <V> value type, should be {@link Cacheable}
 */
abstract class AbstractCache<K, V extends Cacheable<K>> implements CacheExtended<K, V> {

    private final Class<V> type;
    private final int maxCacheSize;

    protected AbstractCache(final Class<V> type, final int maxCacheSize) {
        this.type = type;
        this.maxCacheSize = maxCacheSize;
    }

    protected int getCacheMaxSize() {
        return maxCacheSize;
    }

    protected Class<V> getType() {
        return type;
    }

    @Override
    public List<V> put(final V value) {
        final List<Map.Entry<K, V>> evictedItems = this.put(value.getIdentifier(), value);
        return evictedItems.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
