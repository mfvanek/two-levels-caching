/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.helpers.LFUCacheHelper;
import com.mfvanek.caching.interfaces.Cacheable;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Thread unsafe implementation of LFU cache (Least Frequently Used).
 *
 * @see <a href="https://en.wikipedia.org/wiki/Least_frequently_used">https://en.wikipedia.org/wiki/Least_frequently_used</a>
 * @param <KeyType>
 * @param <ValueType>
 */
public class LFUCache<KeyType, ValueType extends Cacheable<KeyType>>
        extends AbstractMapCache<KeyType, ValueType> {

    private final LFUCacheHelper<KeyType> helper;

    public LFUCache(Class<ValueType> type, int maxCacheSize, float evictionFactor) {
        super(type, maxCacheSize, new HashMap<>(maxCacheSize));
        helper = new LFUCacheHelper<>(evictionFactor);
    }

    @Override
    public List<Map.Entry<KeyType, ValueType>> put(KeyType key, ValueType value) {
        List<Map.Entry<KeyType, ValueType>> evictedItems = Collections.emptyList();
        final ValueType currentValue = getInnerMap().get(key);
        if (currentValue == null) {
            if (isCacheMaxSizeReached()) {
                evictedItems = doEviction();
            }
            helper.rememberFrequency(0, key);
        }
        getInnerMap().put(key, value);
        return evictedItems;
    }

    @Override
    public ValueType get(KeyType key) {
        final ValueType value = super.get(key);
        if (value != null) {
            helper.updateFrequency(key);
        }
        return value;
    }

    @Override
    public ValueType remove(KeyType key) {
        return innerRemove(key).getValue();
    }

    @Override
    public Map.Entry<Integer, ValueType> innerRemove(KeyType key) {
        Integer frequency = INVALID_FREQUENCY;
        final ValueType deletedValue = super.remove(key);
        if (deletedValue != null) {
            frequency = helper.removeKeyFromFrequenciesList(key);
        }
        return new AbstractMap.SimpleEntry<>(frequency, deletedValue);
    }

    @Override
    public void clear() {
        super.clear();
        helper.clear();
    }

    @Override
    public int frequencyOf(KeyType key) {
        return helper.frequencyOf(key);
    }

    @Override
    public int getLowestFrequency() {
        return helper.getLowestFrequency();
    }

    private List<Map.Entry<KeyType, ValueType>> doEviction() {
        // This method will be called only when cache is full
        final List<Map.Entry<KeyType, ValueType>> evictedItems = new LinkedList<>();
        final float target = getCacheMaxSize() * helper.getEvictionFactor();
        int currentlyDeleted = 0;
        while (currentlyDeleted < target) {
            Iterator<KeyType> it = helper.iteratorForLowestFrequency();
            while (it.hasNext() && currentlyDeleted++ < target) {
                final KeyType key = it.next();
                final ValueType value = super.remove(key);
                helper.removeKeyOnEviction(key);
                it.remove();
                evictedItems.add(new AbstractMap.SimpleEntry<>(key, value));
            }
        }
        return evictedItems;
    }
}
