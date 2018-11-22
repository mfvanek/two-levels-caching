/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cacheable;

import java.util.*;

/**
 * Thread unsafe implementation of LFU cache (Least Frequently Used).
 * @see <a href="https://en.wikipedia.org/wiki/Least_frequently_used">https://en.wikipedia.org/wiki/Least_frequently_used</a>
 * @param <KeyType>
 * @param <ValueType>
 */
public class LFUCache<KeyType, ValueType extends Cacheable<KeyType>> extends AbstractMapCache<KeyType, ValueType> {

    private final float evictionFactor;
    private final Map<Integer, Set<KeyType>> frequenciesList;
    private final Map<KeyType, Integer> innerFrequencyMap;

    public LFUCache(int maxCacheSize, float evictionFactor) {
        super(maxCacheSize, new HashMap<>(maxCacheSize));
        if (evictionFactor <= 0.0f || evictionFactor > 1.0f) {
            throw new IllegalArgumentException("Eviction factor must be greater than 0 and less than or equal to 1");
        }
        this.evictionFactor = evictionFactor;
        this.frequenciesList = new TreeMap<>();
        this.innerFrequencyMap = new HashMap<>();
    }

    @Override
    public List<Map.Entry<KeyType, ValueType>> put(KeyType key, ValueType value) {
        List<Map.Entry<KeyType, ValueType>> evictedItems = Collections.emptyList();
        final ValueType currentValue = getInnerMap().get(key);
        if (currentValue == null) {
            if (isCacheMaxSizeReached()) {
                evictedItems = doEviction();
            }
            rememberFrequency(0, key);
        }
        getInnerMap().put(key, value);
        return evictedItems;
    }

    private void rememberFrequency(Integer frequency, KeyType key) {
        Set<KeyType> keys = frequenciesList.get(frequency);
        if (keys == null) {
            keys = new HashSet<>(Collections.singletonList(key));
            frequenciesList.put(frequency, keys);
        } else {
            keys.add(key);
        }
        innerFrequencyMap.put(key, frequency);
    }

    @Override
    public ValueType get(KeyType key) {
        final ValueType value = super.get(key);
        if (value != null) {
            final Integer frequency = innerFrequencyMap.get(key);
            removeKeyFromFrequenciesList(key, frequency);
            rememberFrequency(frequency + 1, key);
        }
        return value;
    }

    @Override
    public ValueType remove(KeyType key) {
        final ValueType value = super.remove(key);
        if (value != null) {
            final Integer frequency = innerFrequencyMap.remove(key);
            removeKeyFromFrequenciesList(key, frequency);
        }
        return value;
    }

    private void removeKeyFromFrequenciesList(KeyType key, Integer frequency) {
        final Set<KeyType> keys = frequenciesList.get(frequency);
        if (keys.size() > 1) {
            keys.remove(key);
        } else {
            frequenciesList.remove(frequency);
        }
    }

    @Override
    public void clear() {
        super.clear();
        frequenciesList.clear();
        innerFrequencyMap.clear();
    }

    public int frequencyOf(KeyType key) throws NoSuchElementException {
        if (containsKey(key)) {
            return innerFrequencyMap.get(key);
        }
        throw new NoSuchElementException("Key " + key + " not found in the cache");
    }

    private List<Map.Entry<KeyType, ValueType>> doEviction() {
        // This method will be called only when cache is full
        final List<Map.Entry<KeyType, ValueType>> evictedItems = new LinkedList<>();
        final float target = getCacheMaxSize() * evictionFactor;
        int currentlyDeleted = 0;
        while (currentlyDeleted < target) {
            final Integer lowestFrequency = getLowestFrequency();
            final Set<KeyType> keys = frequenciesList.get(lowestFrequency);
            Iterator<KeyType> it = keys.iterator();
            while (it.hasNext() && currentlyDeleted++ < target) {
                final KeyType key = it.next();
                final ValueType value = super.remove(key);
                innerFrequencyMap.remove(key);
                it.remove();
                evictedItems.add(new AbstractMap.SimpleEntry<>(key, value));
            }
        }
        return evictedItems;
    }

    private Integer getLowestFrequency() {
        Optional<Integer> minFrequency = frequenciesList.keySet().stream().min(Integer::compareTo);
        return minFrequency.orElse(0);
    }
}
