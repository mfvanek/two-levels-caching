/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.builders;

import io.github.mfvanek.caching.enums.CacheType;
import io.github.mfvanek.caching.impl.TwoLevelsCache;
import io.github.mfvanek.caching.interfaces.Cacheable;
import io.github.mfvanek.caching.interfaces.LeveledCache;

import java.io.Serializable;
import java.nio.file.Path;

public final class TwoLevelsCacheBuilder<K, V extends Cacheable<K> & Serializable> {

    private final Class<V> type;
    private int firstLevelMaxSize = CacheBuilder.DEFAULT_MAX_SIZE;
    private int secondLevelMaxSize = CacheBuilder.DEFAULT_MAX_SIZE;
    private float firstLevelEvictionFactor = CacheBuilder.DEFAULT_EVICTION_FACTOR;
    private float secondLevelEvictionFactor = CacheBuilder.DEFAULT_EVICTION_FACTOR;
    private Path baseDirectory = CacheBuilder.getDefaultBaseDirectory();

    private TwoLevelsCacheBuilder(final Class<V> type) {
        this.type = type;
    }

    public LeveledCache<K, V> build() {
        final CacheBuilder<K, V> builder = CacheBuilder.builder(type);
        final LeveledCache<K, V> firstLevel = builder.setCacheType(CacheType.LFU)
                .setMaxSize(firstLevelMaxSize)
                .setEvictionFactor(firstLevelEvictionFactor)
                .build();
        final LeveledCache<K, V> secondLevel = builder.setCacheType(CacheType.PERSISTENCE_LFU)
                .setMaxSize(secondLevelMaxSize)
                .setEvictionFactor(secondLevelEvictionFactor)
                .setBaseDirectory(baseDirectory)
                .build();
        return new TwoLevelsCache<>(firstLevel, secondLevel);
    }

    public TwoLevelsCacheBuilder<K, V> setFirstLevelMaxSize(final int maxCacheSize) {
        this.firstLevelMaxSize = maxCacheSize;
        return this;
    }

    public TwoLevelsCacheBuilder<K, V> setFirstLevelEvictionFactor(final float evictionFactor) {
        this.firstLevelEvictionFactor = evictionFactor;
        return this;
    }

    public TwoLevelsCacheBuilder<K, V> setSecondLevelMaxSize(final int maxCacheSize) {
        this.secondLevelMaxSize = maxCacheSize;
        return this;
    }

    public TwoLevelsCacheBuilder<K, V> setSecondLevelEvictionFactor(final float evictionFactor) {
        this.secondLevelEvictionFactor = evictionFactor;
        return this;
    }

    public TwoLevelsCacheBuilder<K, V> setBaseDirectory(final Path baseDirectory) {
        this.baseDirectory = baseDirectory;
        return this;
    }

    public static <K, V extends Cacheable<K> & Serializable> TwoLevelsCacheBuilder<K, V> builder(final Class<V> type) {
        return new TwoLevelsCacheBuilder<>(type);
    }
}
