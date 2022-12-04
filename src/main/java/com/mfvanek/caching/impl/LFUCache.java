/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.helpers.LFUCacheHelper;
import com.mfvanek.caching.interfaces.Cacheable;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Thread unsafe implementation of LFU cache (Least Frequently Used).
 *
 * @param <K> key type
 * @param <V> value type, should be {@link Cacheable}
 * @see <a href="https://en.wikipedia.org/wiki/Least_frequently_used">https://en.wikipedia.org/wiki/Least_frequently_used</a>
 */
public class LFUCache<K, V extends Cacheable<K>> extends AbstractMapCache<K, V> {

    private final LFUCacheHelper<K> helper;

    public LFUCache(final Class<V> type, final int maxCacheSize, final float evictionFactor) {
        super(type, maxCacheSize, new HashMap<>(maxCacheSize));
        helper = new LFUCacheHelper<>(evictionFactor);
    }

    @Override
    public List<Map.Entry<K, V>> put(final K key, final V value) {
        List<Map.Entry<K, V>> evictedItems = List.of();
        final V currentValue = getInnerMap().get(key);
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
    public V get(final K key) {
        final V value = super.get(key);
        if (value != null) {
            helper.updateFrequency(key);
        }
        return value;
    }

    @Override
    public V remove(final K key) {
        return innerRemove(key).getValue();
    }

    @Override
    public Map.Entry<Integer, V> innerRemove(final K key) {
        Integer frequency = INVALID_FREQUENCY;
        final V deletedValue = super.remove(key);
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
                final V value = super.remove(key);
                helper.removeKeyOnEviction(key);
                it.remove();
                evictedItems.add(new AbstractMap.SimpleEntry<>(key, value));
            }
        }
        return evictedItems;
    }
}
