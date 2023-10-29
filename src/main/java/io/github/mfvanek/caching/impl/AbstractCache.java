/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.impl;

import io.github.mfvanek.caching.interfaces.Cacheable;
import io.github.mfvanek.caching.interfaces.LeveledCache;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract base class for implementing cache.
 *
 * @param <K> key type
 * @param <V> value type, should be {@link Cacheable}
 */
abstract class AbstractCache<K, V extends Cacheable<K>> implements LeveledCache<K, V> {

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

    protected abstract Map.Entry<Integer, V> innerRemove(K key);
}
