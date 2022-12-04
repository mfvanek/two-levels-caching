/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.helpers;

import com.mfvanek.caching.interfaces.Countable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public class LFUCacheHelper<K> implements Countable<K> {

    private final float evictionFactor;
    private final Map<Integer, Set<K>> frequenciesList;
    private final Map<K, Integer> innerFrequencyMap;

    public LFUCacheHelper(final float evictionFactor) {
        LFUCacheHelper.validateEvictionFactor(evictionFactor);

        this.evictionFactor = evictionFactor;
        this.frequenciesList = new TreeMap<>();
        this.innerFrequencyMap = new HashMap<>();
    }

    public float getEvictionFactor() {
        return evictionFactor;
    }

    private static void validateEvictionFactor(final float evictionFactor) {
        if (evictionFactor <= 0.0f || evictionFactor > 1.0f) {
            throw new IllegalArgumentException("Eviction factor must be greater than 0 and less than or equal to 1");
        }
    }

    @Override
    public int getLowestFrequency() {
        Optional<Integer> minFrequency = frequenciesList.keySet().stream().min(Integer::compareTo);
        return minFrequency.orElse(0);
    }

    @Override
    public int frequencyOf(final K key) {
        if (innerFrequencyMap.containsKey(key)) {
            return innerFrequencyMap.get(key);
        }
        throw new NoSuchElementException("Key " + key + " not found in the cache");
    }

    public void clear() {
        frequenciesList.clear();
        innerFrequencyMap.clear();
    }

    public void removeKeyOnEviction(final K key) {
        innerFrequencyMap.remove(key);
    }

    public Integer removeKeyFromFrequenciesList(final K key) {
        final Integer frequency = innerFrequencyMap.remove(key);
        removeKeyFromFrequenciesList(key, frequency);
        return frequency;
    }

    private void removeKeyFromFrequenciesList(final K key, final Integer frequency) {
        final Set<K> keys = frequenciesList.get(frequency);
        if (keys.size() > 1) {
            keys.remove(key);
        } else {
            frequenciesList.remove(frequency);
        }
    }

    public void rememberFrequency(final Integer frequency, final K key) {
        Set<K> keys = frequenciesList.get(frequency);
        if (keys == null) {
            keys = new HashSet<>(Set.of(key));
            frequenciesList.put(frequency, keys);
        } else {
            keys.add(key);
        }
        innerFrequencyMap.put(key, frequency);
    }

    public void updateFrequency(final K key) {
        final Integer frequency = innerFrequencyMap.get(key);
        removeKeyFromFrequenciesList(key, frequency);
        rememberFrequency(frequency + 1, key);
    }

    public Iterator<K> iteratorForLowestFrequency() {
        // We need to remove entries with empty values
        frequenciesList.entrySet().removeIf(e -> CollectionUtils.isEmpty(e.getValue()));

        final Integer lowestFrequency = getLowestFrequency();
        final Set<K> keys = frequenciesList.get(lowestFrequency);
        return keys.iterator();
    }
}
