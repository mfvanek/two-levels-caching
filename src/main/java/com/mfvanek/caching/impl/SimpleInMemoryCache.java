/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cacheable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The simplest thread unsafe implementation of in-memory cache
 * @param <KeyType>
 * @param <ValueType>
 */
public final class SimpleInMemoryCache<KeyType, ValueType extends Cacheable<KeyType>> extends AbstractMapCache<KeyType, ValueType> {

    public SimpleInMemoryCache(int maxCacheSize) {
        super(maxCacheSize, new LinkedHashMap<KeyType, ValueType>(maxCacheSize) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<KeyType, ValueType> eldest) {
                return size() > maxCacheSize;
            }
        });
    }

    @Override
    public List<Map.Entry<KeyType, ValueType>> put(KeyType key, ValueType value) {
        getInnerMap().put(key, value);
        // In this case we always return an empty list because we don't control eviction from the cache
        return Collections.emptyList();
    }
}
