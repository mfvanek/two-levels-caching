/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.builders;

import com.mfvanek.caching.enums.CacheType;
import com.mfvanek.caching.exceptions.InvalidCacheTypeException;
import com.mfvanek.caching.impl.LFUCache;
import com.mfvanek.caching.impl.PersistenceLFUCache;
import com.mfvanek.caching.impl.SimpleInMemoryCache;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Cacheable;
import org.apache.commons.lang3.SystemUtils;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CacheBuilder<K, V extends Cacheable<K> & Serializable> {

    public static final int DEFAULT_MAX_SIZE = 10;
    public static final float DEFAULT_EVICTION_FACTOR = 0.2f;

    private final Class<V> type;
    private int maxCacheSize = DEFAULT_MAX_SIZE;
    private float evictionFactor = DEFAULT_EVICTION_FACTOR;
    private CacheType cacheType = CacheType.SIMPLE;
    private Path baseDirectory = getDefaultBaseDirectory();

    private CacheBuilder(final Class<V> type) {
        this.type = type;
    }

    public Cache<K, V> build() {
        switch (cacheType) {
            case SIMPLE:
                return new SimpleInMemoryCache<>(type, maxCacheSize);
            case LFU:
                return new LFUCache<>(type, maxCacheSize, evictionFactor);
            case PERSISTENCE_LFU:
                return new PersistenceLFUCache<>(type, maxCacheSize, evictionFactor, baseDirectory);
            default:
                throw new InvalidCacheTypeException(cacheType);
        }
    }

    public CacheBuilder<K, V> setMaxSize(final int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        return this;
    }

    public CacheBuilder<K, V> setEvictionFactor(final float evictionFactor) {
        this.evictionFactor = evictionFactor;
        return this;
    }

    public CacheBuilder<K, V> setCacheType(final CacheType cacheType) {
        this.cacheType = cacheType;
        return this;
    }

    public CacheBuilder<K, V> setBaseDirectory(final Path baseDirectory) {
        this.baseDirectory = baseDirectory;
        return this;
    }

    public static Path getDefaultBaseDirectory() {
        if (SystemUtils.IS_OS_MAC) {
            return Paths.get(System.getProperty("user.home"), "Library/Caches", "jcache")
                    .toAbsolutePath();
        }
        if (SystemUtils.IS_OS_LINUX) {
            return Paths.get("/var/tmp/", "jcache")
                    .toAbsolutePath();
        }
        return Paths.get(".")
                .resolve("/jcache/")
                .toAbsolutePath();
    }

    public static <K, V extends Cacheable<K> & Serializable> CacheBuilder<K, V> getInstance(final Class<V> type) {
        return new CacheBuilder<>(type);
    }
}
