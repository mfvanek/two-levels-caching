/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Cacheable;

abstract class AbstractCache<KeyType, ValueType extends Cacheable<KeyType>> implements Cache<KeyType, ValueType> {

    private final int cacheMaxSize;

    protected AbstractCache(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

    protected int getCacheMaxSize() {
        return cacheMaxSize;
    }

    @Override
    public void put(ValueType value) {
        this.put(value.getIdentifier(), value);
    }
}
