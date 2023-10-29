/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.builders;

import io.github.mfvanek.caching.enums.CacheType;
import io.github.mfvanek.caching.impl.LFUCache;
import io.github.mfvanek.caching.impl.PersistenceLFUCache;
import io.github.mfvanek.caching.impl.SimpleInMemoryCache;
import io.github.mfvanek.caching.interfaces.Cacheable;
import io.github.mfvanek.caching.interfaces.LeveledCache;
import org.apache.commons.lang3.SystemUtils;

import java.io.Serializable;
import java.nio.file.Path;

public final class CacheBuilder<K, V extends Cacheable<K> & Serializable> {

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

    public LeveledCache<K, V> build() {
        return switch (cacheType) {
            case LFU -> new LFUCache<>(type, maxCacheSize, evictionFactor);
            case PERSISTENCE_LFU -> new PersistenceLFUCache<>(type, maxCacheSize, evictionFactor, baseDirectory);
            case SIMPLE -> new SimpleInMemoryCache<>(type, maxCacheSize);
        };
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
            return DefaultBaseDirectoryHelper.forMacOs();
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            return DefaultBaseDirectoryHelper.forWindows();
        }
        return DefaultBaseDirectoryHelper.forLinux();
    }

    public static <K, V extends Cacheable<K> & Serializable> CacheBuilder<K, V> builder(final Class<V> type) {
        return new CacheBuilder<>(type);
    }
}
