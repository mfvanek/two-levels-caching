/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.helpers;

import io.github.mfvanek.caching.interfaces.Countable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class LFUCacheHelper<K> implements Countable<K> {

    private final float evictionFactor;
    private final SortedMap<Integer, Set<K>> frequencies;
    private final Map<K, Integer> innerFrequencyMap;

    public LFUCacheHelper(final float evictionFactor) {
        LFUCacheHelper.validateEvictionFactor(evictionFactor);

        this.evictionFactor = evictionFactor;
        this.frequencies = new TreeMap<>();
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
        return frequencies.keySet()
                .stream()
                .min(Integer::compareTo)
                .orElse(0);
    }

    @Override
    public int frequencyOf(final K key) {
        final Integer value = innerFrequencyMap.get(key);
        if (value != null) {
            return value;
        }
        throw new NoSuchElementException("Key " + key + " not found in the cache");
    }

    public void clear() {
        frequencies.clear();
        innerFrequencyMap.clear();
    }

    public void removeKeyOnEviction(final K key) {
        innerFrequencyMap.remove(key);
    }

    public Integer removeKeyFromFrequencies(final K key) {
        final Integer frequency = innerFrequencyMap.remove(key);
        removeKeyFromFrequencies(key, frequency);
        return frequency;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private void removeKeyFromFrequencies(final K key, final Integer frequency) {
        final Set<K> keys = frequencies.get(frequency);
        if (keys.size() > 1) {
            keys.remove(key);
        } else {
            frequencies.remove(frequency);
        }
    }

    public void rememberFrequency(final Integer frequency, final K key) {
        Set<K> keys = frequencies.get(frequency);
        if (keys == null) {
            keys = new HashSet<>(Set.of(key));
            frequencies.put(frequency, keys);
        } else {
            keys.add(key);
        }
        innerFrequencyMap.put(key, frequency);
    }

    public void updateFrequency(final K key) {
        final Integer frequency = innerFrequencyMap.get(key);
        removeKeyFromFrequencies(key, frequency);
        rememberFrequency(frequency + 1, key);
    }

    public Iterator<K> iteratorForLowestFrequency() {
        // We need to remove entries with empty values
        frequencies.entrySet().removeIf(e -> CollectionUtils.isEmpty(e.getValue()));

        final Integer lowestFrequency = getLowestFrequency();
        final Set<K> keys = frequencies.get(lowestFrequency);
        return keys.iterator();
    }
}
