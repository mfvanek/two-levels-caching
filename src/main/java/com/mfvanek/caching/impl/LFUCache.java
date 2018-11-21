/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cacheable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LFUCache<KeyType, ValueType extends Cacheable<KeyType>> extends AbstractMapCache<KeyType, ValueType> {

    private final float evictionFactor;
    private final Map<Integer, Set<KeyType>> frequenciesList;
    private final Map<KeyType, Integer> innerFrequencyMap;

    public LFUCache(int maxCacheSize, float evictionFactor) {
        super(maxCacheSize, new HashMap<>(maxCacheSize));
        if (evictionFactor <= 0 || evictionFactor >= 1) {
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
            frequenciesList.get(frequency).remove(key);
            rememberFrequency(frequency + 1, key);
        }
        return value;
    }

    @Override
    public ValueType remove(KeyType key) {
        final ValueType value = super.remove(key);
        if (value != null) {
            final Integer frequency = innerFrequencyMap.remove(key);
            frequenciesList.get(frequency).remove(key);
        }
        return value;
    }

    @Override
    public void clear() {
        super.clear();
        frequenciesList.clear();
        innerFrequencyMap.clear();
    }

    private List<Map.Entry<KeyType, ValueType>> doEviction() {
        final List<Map.Entry<KeyType, ValueType>> evictedItems = new LinkedList<>();
        /* TODO
        final float target = getCacheMaxSize() * evictionFactor;
        while (currentlyDeleted < target) {
            LinkedHashSet<CacheNode<Key, Value>> nodes = frequencyList[lowestFrequency];
            if (nodes.isEmpty()) {
                throw new IllegalStateException("Lowest frequency constraint violated!");
            } else {
                Iterator<CacheNode<Key, Value>> it = nodes.iterator();
                while (it.hasNext() && currentlyDeleted++ < target) {
                    CacheNode<Key, Value> node = it.next();
                    it.remove();
                    cache.remove(node.k);
                }
                if (!it.hasNext()) {
                    findNextLowestFrequency();
                }
            }
        }
        */
        return evictedItems;
    }
}
