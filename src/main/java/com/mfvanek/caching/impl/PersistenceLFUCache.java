/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.helpers.LFUCacheHelper;
import com.mfvanek.caching.interfaces.Cacheable;
import org.apache.commons.lang3.NotImplementedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PersistenceLFUCache<KeyType, ValueType extends Cacheable<KeyType> & Serializable>
        extends AbstractCache<KeyType, ValueType> {

    private static final String EXTENSION = ".ser";

    private final LFUCacheHelper<KeyType> helper;
    private final Map<KeyType, Path> innerMap;
    private final Path baseDirectory;

    /**
     * Creates an instance of @see {PersistenceLFUCache} class
     * @param maxCacheSize The maximum number of items that can be placed in the cache
     * @param evictionFactor The percentage of items that should be removed from the cache when it is full
     * @param baseDirectory The directory in which the cached data will be saved. If the directory doesn't exist, it will be created.
     */
    public PersistenceLFUCache(Class<ValueType> type, int maxCacheSize, float evictionFactor, Path baseDirectory)
            throws IOException {
        super(type, maxCacheSize);
        this.helper = new LFUCacheHelper<>(evictionFactor);
        this.innerMap = new HashMap<>(maxCacheSize);
        this.baseDirectory = baseDirectory;

        Files.createDirectories(baseDirectory);
    }

    @Override
    public List<Map.Entry<KeyType, ValueType>> put(KeyType key, ValueType value)
            throws IOException, ClassNotFoundException {
        List<Map.Entry<KeyType, ValueType>> evictedItems = Collections.emptyList();
        final Path cacheFilePath = innerMap.get(key);
        if (cacheFilePath == null) {
            if (isCacheMaxSizeReached()) {
                evictedItems = doEviction();
            }
            helper.rememberFrequency(0, key);
        }
        innerMap.put(key, serialize(value));
        return evictedItems;
    }

    @Override
    public ValueType get(KeyType key) {
        throw new NotImplementedException(""); // TODO
    }

    @Override
    public boolean containsKey(KeyType key) {
        return innerMap.containsKey(key);
    }

    @Override
    public ValueType remove(KeyType key) throws IOException, ClassNotFoundException {
        helper.removeKeyFromFrequenciesList(key);
        return doRemove(key);
    }

    private ValueType doRemove(KeyType key) throws IOException, ClassNotFoundException {
        ValueType deletedValue = null;
        final Path cacheFilePath = innerMap.remove(key);
        if (cacheFilePath != null) {
            deletedValue = deserialize(cacheFilePath);
            Files.deleteIfExists(cacheFilePath);
        }
        return deletedValue;
    }

    @Override
    public void clear() throws IOException {
        for (Map.Entry<KeyType, Path> entry : innerMap.entrySet()) {
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

    private ValueType deserialize(final Path cacheFilePath) throws IOException, ClassNotFoundException {
        final byte[] data = Files.readAllBytes(cacheFilePath);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return getType().cast(ois.readObject());
        }
    }

    private Path serialize(ValueType value) throws IOException {
        final Path cacheFilePath = baseDirectory.resolve(UUID.randomUUID() + EXTENSION);
        try (FileChannel channel = FileChannel.open(cacheFilePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream ous = new ObjectOutputStream(bos)) {
            ous.writeObject(value);
            channel.write(ByteBuffer.wrap(bos.toByteArray()));
        }
        return cacheFilePath;
    }

    int frequencyOf(KeyType key) throws NoSuchElementException {
        return helper.frequencyOf(key);
    }

    private List<Map.Entry<KeyType, ValueType>> doEviction() throws IOException, ClassNotFoundException {
        // This method will be called only when cache is full
        final List<Map.Entry<KeyType, ValueType>> evictedItems = new LinkedList<>();
        final float target = getCacheMaxSize() * helper.getEvictionFactor();
        int currentlyDeleted = 0;
        while (currentlyDeleted < target) {
            Iterator<KeyType> it = helper.iteratorForLowestFrequency();
            while (it.hasNext() && currentlyDeleted++ < target) {
                final KeyType key = it.next();
                final ValueType value = doRemove(key);
                helper.removeKeyOnEviction(key);
                it.remove();
                evictedItems.add(new AbstractMap.SimpleEntry<>(key, value));
            }
        }
        return evictedItems;
    }
}
