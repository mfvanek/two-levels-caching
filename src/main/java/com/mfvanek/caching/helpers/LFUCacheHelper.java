/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.helpers;

import com.mfvanek.caching.interfaces.Countable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public class LFUCacheHelper<KeyType> implements Countable<KeyType> {

    private final float evictionFactor;
    private final Map<Integer, Set<KeyType>> frequenciesList;
    private final Map<KeyType, Integer> innerFrequencyMap;

    public LFUCacheHelper(float evictionFactor) {
        LFUCacheHelper.ValidateEvictionFactor(evictionFactor);

        this.evictionFactor = evictionFactor;
        this.frequenciesList = new TreeMap<>();
        this.innerFrequencyMap = new HashMap<>();
    }

    public float getEvictionFactor() {
        return evictionFactor;
    }

    private static void ValidateEvictionFactor(float evictionFactor) {
        if (evictionFactor <= 0.0f || evictionFactor > 1.0f) {
            throw new IllegalArgumentException("Eviction factor must be greater than 0 and less than or equal to 1");
        }
    }

    private Integer getLowestFrequency() {
        // TODO Error!!!!
        Optional<Integer> minFrequency = frequenciesList.keySet().stream().min(Integer::compareTo);
        return minFrequency.orElse(0);
    }

    @Override
    public int frequencyOf(KeyType key) throws NoSuchElementException {
        if (innerFrequencyMap.containsKey(key)) {
            return innerFrequencyMap.get(key);
        }
        throw new NoSuchElementException("Key " + key + " not found in the cache");
    }

    public void clear() {
        frequenciesList.clear();
        innerFrequencyMap.clear();
    }

    public void removeKeyOnEviction(KeyType key) {
        innerFrequencyMap.remove(key);
    }

    public void removeKeyFromFrequenciesList(KeyType key) {
        final Integer frequency = innerFrequencyMap.remove(key);
        removeKeyFromFrequenciesList(key, frequency);
    }

    private void removeKeyFromFrequenciesList(KeyType key, Integer frequency) {
        final Set<KeyType> keys = frequenciesList.get(frequency);
        if (keys.size() > 1) {
            keys.remove(key);
        } else {
            frequenciesList.remove(frequency);
        }
    }

    public void rememberFrequency(Integer frequency, KeyType key) {
        Set<KeyType> keys = frequenciesList.get(frequency);
        if (keys == null) {
            keys = new HashSet<>(Collections.singletonList(key));
            frequenciesList.put(frequency, keys);
        } else {
            keys.add(key);
        }
        innerFrequencyMap.put(key, frequency);
    }

    public void updateFrequency(KeyType key) {
        final Integer frequency = innerFrequencyMap.get(key);
        removeKeyFromFrequenciesList(key, frequency);
        rememberFrequency(frequency + 1, key);
    }

    public Iterator<KeyType> iteratorForLowestFrequency() {
        final Integer lowestFrequency = getLowestFrequency();
        final Set<KeyType> keys = frequenciesList.get(lowestFrequency);
        return keys.iterator();
    }
}
