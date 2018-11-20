/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.builders;

import com.mfvanek.caching.impl.SimpleInMemoryCache;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Cacheable;

public class CacheBuilder<KeyType, ValueType extends Cacheable<KeyType>>  {

    public static final int DEFAULT_MAX_SIZE = 10;

    private int cacheMaxSize = DEFAULT_MAX_SIZE;

    private CacheBuilder() {
    }

    public Cache<KeyType, ValueType> build() {
        return new SimpleInMemoryCache<>(cacheMaxSize);
    }

    public CacheBuilder<KeyType, ValueType> setMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
        return this;
    }

    public static <KeyType, ValueType extends Cacheable<KeyType>> CacheBuilder<KeyType, ValueType> getInstance() {
        return new CacheBuilder<>();
    }
}
