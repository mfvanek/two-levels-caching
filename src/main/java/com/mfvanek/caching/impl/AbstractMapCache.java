/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cacheable;

import java.util.Map;

abstract class AbstractMapCache<KeyType, ValueType extends Cacheable<KeyType>> extends AbstractCache<KeyType, ValueType> {

    private final Map<KeyType, ValueType> innerMap;

    protected AbstractMapCache(int maxCacheSize, Map<KeyType, ValueType> innerMap) {
        super(maxCacheSize);
        this.innerMap = innerMap;
    }

    protected Map<KeyType, ValueType> getInnerMap() {
        return innerMap;
    }

    protected boolean isCacheMaxSizeReached() {
        return innerMap.size() == getCacheMaxSize();
    }

    @Override
    public ValueType get(KeyType key) {
        return innerMap.get(key);
    }

    @Override
    public boolean containsKey(KeyType key) {
        return innerMap.containsKey(key);
    }

    @Override
    public ValueType remove(KeyType key) {
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
