/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.builders;

import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.impl.TwoLevelsCache;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Cacheable;

import java.io.Serializable;
import java.nio.file.Path;

public class TwoLevelsCacheBuilder<KeyType, ValueType extends Cacheable<KeyType> & Serializable> {

    private final Class<ValueType> type;
    private int firstLevelMaxSize = CacheBuilder.DEFAULT_MAX_SIZE;
    private int secondLevelMaxSize = CacheBuilder.DEFAULT_MAX_SIZE;
    private float firstLevelEvictionFactor = CacheBuilder.DEFAULT_EVICTION_FACTOR;
    private float secondLevelEvictionFactor = CacheBuilder.DEFAULT_EVICTION_FACTOR;
    private Path baseDirectory = CacheBuilder.getDefaultBaseDirectory();

    private TwoLevelsCacheBuilder(Class<ValueType> type) {
        this.type = type;
    }

    public Cache<KeyType, ValueType> build() throws Exception {
        final  CacheBuilder<KeyType, ValueType> builder = CacheBuilder.getInstance(type);
        final Cache<KeyType, ValueType> firstLevel = builder.setCacheType(CacheType.LFU).
                setMaxSize(firstLevelMaxSize).setEvictionFactor(firstLevelEvictionFactor).build();
        final Cache<KeyType, ValueType> secondLevel = builder.setCacheType(CacheType.PERSISTENCE_LFU).
                setMaxSize(secondLevelMaxSize).setEvictionFactor(secondLevelEvictionFactor).
                setBaseDirectory(baseDirectory).build();
        return new TwoLevelsCache<>(firstLevel, secondLevel);
    }

    public TwoLevelsCacheBuilder<KeyType, ValueType> setFirstLevelMaxSize(int maxCacheSize) {
        this.firstLevelMaxSize = maxCacheSize;
        return this;
    }

    public TwoLevelsCacheBuilder<KeyType, ValueType> setFirstLevelEvictionFactor(float evictionFactor) {
        this.firstLevelEvictionFactor = evictionFactor;
        return this;
    }

    public TwoLevelsCacheBuilder<KeyType, ValueType> setSecondLevelMaxSize(int maxCacheSize) {
        this.secondLevelMaxSize = maxCacheSize;
        return this;
    }

    public TwoLevelsCacheBuilder<KeyType, ValueType> setSecondLevelEvictionFactor(float evictionFactor) {
        this.secondLevelEvictionFactor = evictionFactor;
        return this;
    }

    public TwoLevelsCacheBuilder<KeyType, ValueType> setBaseDirectory(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
        return this;
    }

    public static <KeyType, ValueType extends Cacheable<KeyType>& Serializable> TwoLevelsCacheBuilder<KeyType, ValueType> getInstance(Class<ValueType> type) {
        return new TwoLevelsCacheBuilder<>(type);
    }
}
