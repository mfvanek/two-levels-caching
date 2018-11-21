/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Cacheable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class AbstractCache<KeyType, ValueType extends Cacheable<KeyType>> implements Cache<KeyType, ValueType> {

    private final int maxCacheSize;

    protected AbstractCache(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    protected int getCacheMaxSize() {
        return maxCacheSize;
    }

    @Override
    public List<ValueType> put(ValueType value) {
        return this.put(value.getIdentifier(), value).
                stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
