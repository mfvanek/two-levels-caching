/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.builders;

import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.exceptions.InvalidCacheTypeException;
import com.mfvanek.caching.impl.LFUCache;
import com.mfvanek.caching.impl.PersistenceLFUCache;
import com.mfvanek.caching.impl.SimpleInMemoryCache;
import com.mfvanek.caching.interfaces.CacheExtended;
import com.mfvanek.caching.interfaces.Cacheable;
import org.apache.commons.lang3.SystemUtils;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CacheBuilder<KeyType, ValueType extends Cacheable<KeyType> & Serializable> {

    public static final int DEFAULT_MAX_SIZE = 10;
    public static final float DEFAULT_EVICTION_FACTOR = 0.2f;

    private final Class<ValueType> type;
    private int maxCacheSize = DEFAULT_MAX_SIZE;
    private float evictionFactor = DEFAULT_EVICTION_FACTOR;
    private CacheType cacheType = CacheType.SIMPLE;
    private Path baseDirectory = getDefaultBaseDirectory();

    private CacheBuilder(Class<ValueType> type) {
        this.type = type;
    }

    public CacheExtended<KeyType, ValueType> build() {
        switch (cacheType) {
            case SIMPLE:
                return new SimpleInMemoryCache<>(type, maxCacheSize);

            case LFU:
                return new LFUCache<>(type, maxCacheSize, evictionFactor);

            case PERSISTENCE_LFU:
                return new PersistenceLFUCache<>(type, maxCacheSize, evictionFactor, baseDirectory);
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

    public CacheBuilder<KeyType, ValueType> setBaseDirectory(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
        return this;
    }

    public static Path getDefaultBaseDirectory() {
        if (SystemUtils.IS_OS_MAC) {
            return Paths.get(System.getProperty("user.home"), "Library/Caches", "jcache")
                    .toAbsolutePath();
        }
        return Paths.get(".")
                .resolve("/jcache/")
                .toAbsolutePath();
    }

    public static <KeyType, ValueType extends Cacheable<KeyType> & Serializable> CacheBuilder<KeyType, ValueType> getInstance(Class<ValueType> type) {
        return new CacheBuilder<>(type);
    }
}
