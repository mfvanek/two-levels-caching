/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.builders;

import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.exceptions.InvalidCacheTypeException;
import com.mfvanek.caching.impl.LFUCache;
import com.mfvanek.caching.impl.SimpleInMemoryCache;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Cacheable;

public class CacheBuilder<KeyType, ValueType extends Cacheable<KeyType>>  {

    public static final int DEFAULT_MAX_SIZE = 10;
    public static final float DEFAULT_EVICTION_FACTOR = 0.2f;

    private int maxCacheSize = DEFAULT_MAX_SIZE;
    private float evictionFactor = DEFAULT_EVICTION_FACTOR;
    private CacheType cacheType = CacheType.SIMPLE;

    private CacheBuilder() {
    }

    public Cache<KeyType, ValueType> build() throws InvalidCacheTypeException {
        switch (cacheType) {
            case SIMPLE:
                return new SimpleInMemoryCache<>(maxCacheSize);

            case LFU:
                return new LFUCache<>(maxCacheSize, evictionFactor);
        }
        throw new InvalidCacheTypeException(cacheType);
    }

    public CacheBuilder<KeyType, ValueType> setMaxSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        return this;
    }

    public CacheBuilder<KeyType, ValueType> setEvictionFactor(float evictionFactor) {
        this.evictionFactor = evictionFactor;
        return this;
    }

    public CacheBuilder<KeyType, ValueType> setCacheType(CacheType cacheType) {
        this.cacheType = cacheType;
        return this;
    }

    public static <KeyType, ValueType extends Cacheable<KeyType>> CacheBuilder<KeyType, ValueType> getInstance() {
        return new CacheBuilder<>();
    }
}
