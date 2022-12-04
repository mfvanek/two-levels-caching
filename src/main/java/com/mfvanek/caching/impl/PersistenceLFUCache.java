/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.helpers.LFUCacheHelper;
import com.mfvanek.caching.helpers.Serializer;
import com.mfvanek.caching.interfaces.Cacheable;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PersistenceLFUCache<K, V extends Cacheable<K> & Serializable> extends AbstractCache<K, V> {

    private static final String EXTENSION = ".ser";

    private final LFUCacheHelper<K> helper;
    private final Map<K, Path> innerMap;
    private final Path baseDirectory;

    /**
     * Creates an instance of {@link PersistenceLFUCache} class.
     *
     * @param maxCacheSize   The maximum number of items that can be placed in the cache
     * @param evictionFactor The percentage of items that should be removed from the cache when it is full
     * @param baseDirectory  The directory in which the cached data will be saved. If the directory doesn't exist, it will be created.
     */
    @SneakyThrows
    public PersistenceLFUCache(final Class<V> type,
                               final int maxCacheSize,
                               final float evictionFactor,
                               final Path baseDirectory) {
        super(type, maxCacheSize);
        this.helper = new LFUCacheHelper<>(evictionFactor);
        this.innerMap = new HashMap<>(maxCacheSize);
        this.baseDirectory = baseDirectory;

        Files.createDirectories(baseDirectory);
    }

    @Override
    public List<Map.Entry<K, V>> put(final K key, final V value) {
        List<Map.Entry<K, V>> evictedItems = List.of();
        Path cacheFilePath = innerMap.get(key);
        if (cacheFilePath == null) {
            cacheFilePath = generateSerializedFilePath();
            if (isCacheMaxSizeReached()) {
                evictedItems = doEviction();
            }
            helper.rememberFrequency(0, key);
        }
        innerMap.put(key, Serializer.serialize(value, cacheFilePath));
        return evictedItems;
    }

    @Override
    public V get(final K key) {
        V value = null;
        final Path cacheFilePath = innerMap.get(key);
        if (cacheFilePath != null) {
            value = Serializer.deserialize(getType(), cacheFilePath);
            helper.updateFrequency(key);
        }
        return value;
    }

    @Override
    public boolean containsKey(final K key) {
        return innerMap.containsKey(key);
    }

    @Override
    public V remove(final K key) {
        return innerRemove(key).getValue();
    }

    @Override
    public Map.Entry<Integer, V> innerRemove(final K key) {
        Integer frequency = INVALID_FREQUENCY;
        final V deletedValue = doRemove(key);
        if (deletedValue != null) {
            frequency = helper.removeKeyFromFrequenciesList(key);
        }
        return new AbstractMap.SimpleEntry<>(frequency, deletedValue);
    }

    @SneakyThrows
    private V doRemove(final K key) {
        V deletedValue = null;
        final Path cacheFilePath = innerMap.remove(key);
        if (cacheFilePath != null) {
            deletedValue = Serializer.deserialize(getType(), cacheFilePath);
            Files.deleteIfExists(cacheFilePath);
        }
        return deletedValue;
    }

    @SneakyThrows
    @Override
    public void clear() {
        for (Map.Entry<K, Path> entry : innerMap.entrySet()) {
            Files.deleteIfExists(entry.getValue());
        }
        innerMap.clear();
        helper.clear();
    }

    @Override
    public int size() {
        return innerMap.size();
    }

    private boolean isCacheMaxSizeReached() {
        return innerMap.size() == getCacheMaxSize();
    }

    private Path generateSerializedFilePath() {
        return baseDirectory.resolve(UUID.randomUUID() + EXTENSION);
    }

    @Override
    public int frequencyOf(final K key) {
        return helper.frequencyOf(key);
    }

    @Override
    public int getLowestFrequency() {
        return helper.getLowestFrequency();
    }

    private List<Map.Entry<K, V>> doEviction() {
        // This method will be called only when cache is full
        final List<Map.Entry<K, V>> evictedItems = new LinkedList<>();
        final float target = getCacheMaxSize() * helper.getEvictionFactor();
        int currentlyDeleted = 0;
        while (currentlyDeleted < target) {
            final Iterator<K> it = helper.iteratorForLowestFrequency();
            while (it.hasNext() && currentlyDeleted++ < target) {
                final K key = it.next();
                final V value = doRemove(key);
                helper.removeKeyOnEviction(key);
                it.remove();
                evictedItems.add(new AbstractMap.SimpleEntry<>(key, value));
            }
        }
        return evictedItems;
    }
}
