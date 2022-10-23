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
 * Abstract base class for implementing cache
 * @param <KeyType>
 * @param <ValueType>
 */
abstract class AbstractCache<KeyType, ValueType extends Cacheable<KeyType>>
        implements CacheExtended<KeyType, ValueType> {

    private final Class<ValueType> type;
    private final int maxCacheSize;

    protected AbstractCache(Class<ValueType> type, int maxCacheSize) {
        this.type = type;
        this.maxCacheSize = maxCacheSize;
    }

    protected int getCacheMaxSize() {
        return maxCacheSize;
    }

    protected Class<ValueType> getType() {
        return type;
    }

    @Override
    public List<ValueType> put(ValueType value) {
        final List<Map.Entry<KeyType, ValueType>> evictedItems =  this.put(value.getIdentifier(), value);
        return evictedItems.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
