/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.impl;

import io.github.mfvanek.caching.interfaces.Cacheable;

import java.util.Map;

abstract class AbstractMapCache<K, V extends Cacheable<K>> extends AbstractCache<K, V> {

    private final Map<K, V> innerMap;

    protected AbstractMapCache(final Class<V> type, final int maxCacheSize, final Map<K, V> innerMap) {
        super(type, maxCacheSize);
        this.innerMap = innerMap;
    }

    protected Map<K, V> getInnerMap() {
        return innerMap;
    }

    protected boolean isCacheMaxSizeReached() {
        return innerMap.size() == getCacheMaxSize();
    }

    @Override
    public V get(final K key) {
        return innerMap.get(key);
    }

    @Override
    public boolean containsKey(final K key) {
        return innerMap.containsKey(key);
    }

    @Override
    public V remove(final K key) {
        return innerMap.remove(key);
    }

    @Override
    public void clear() {
        innerMap.clear();
    }

    @Override
    public int size() {
        return innerMap.size();
    }
}
