/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cacheable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleInMemoryCache<KeyType, ValueType extends Cacheable<KeyType>> extends AbstractCache<KeyType, ValueType> {

    private final Map<KeyType, ValueType> innerMap;

    public SimpleInMemoryCache(int cacheMaxSize) {
        super(cacheMaxSize);
        innerMap = new LinkedHashMap<KeyType, ValueType>(cacheMaxSize) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<KeyType, ValueType> eldest) {
                return size() > getCacheMaxSize();
            }
        };
    }

    @Override
    public void put(KeyType key, ValueType value) {
        innerMap.put(key, value);
    }

    public ValueType get(KeyType key) {
        return innerMap.get(key);
    }

    public boolean containsKey(KeyType key) {
        return innerMap.containsKey(key);
    }

    public ValueType remove(KeyType key) {
        return innerMap.remove(key);
    }

    public void clear() {
        innerMap.clear();
    }

    @Override
    public int size() {
        return innerMap.size();
    }
}
