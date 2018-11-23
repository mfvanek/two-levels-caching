/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.Cacheable;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TwoLevelsCache<KeyType, ValueType extends Cacheable<KeyType> & Serializable>
        implements Cache<KeyType, ValueType> {

    private final Cache<KeyType, ValueType> firstLevel;
    private final Cache<KeyType, ValueType> secondLevel;

    public TwoLevelsCache(Cache<KeyType, ValueType> firstLevel, Cache<KeyType, ValueType> secondLevel) {
        this.firstLevel = firstLevel;
        this.secondLevel = secondLevel;
    }

    @Override
    public List<Map.Entry<KeyType, ValueType>> put(KeyType key, ValueType value) throws Exception {
        final List<Map.Entry<KeyType, ValueType>> firstLevelEvictedItems = firstLevel.put(key, value);
        if (CollectionUtils.isNotEmpty(firstLevelEvictedItems)) {
            final List<Map.Entry<KeyType, ValueType>> evictedItems = new LinkedList<>();
            for (Map.Entry<KeyType, ValueType> entry : firstLevelEvictedItems) {
                final List<Map.Entry<KeyType, ValueType>> secondLevelEvictedItems = secondLevel.put(entry.getKey(), entry.getValue());
                evictedItems.addAll(secondLevelEvictedItems);
            }
            return evictedItems;
        }
        return firstLevelEvictedItems;
    }

    @Override
    public List<ValueType> put(ValueType value) throws Exception {
        final List<Map.Entry<KeyType, ValueType>> evictedItems =  this.put(value.getIdentifier(), value);
        return evictedItems.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public ValueType get(KeyType key) throws Exception {
        ValueType foundItem = firstLevel.get(key);
        if (foundItem == null) {
            foundItem = secondLevel.get(key);
        }
        return foundItem;
    }

    @Override
    public boolean containsKey(KeyType key) {
        return firstLevel.containsKey(key) || secondLevel.containsKey(key);
    }

    @Override
    public ValueType remove(KeyType key) throws Exception {
        ValueType deletedItem = firstLevel.remove(key);
        if (deletedItem == null) {
            deletedItem = secondLevel.remove(key);
        }
        return deletedItem;
    }

    @Override
    public void clear() throws Exception {
        firstLevel.clear();
        secondLevel.clear();
    }

    @Override
    public int size() {
        return firstLevel.size() + secondLevel.size();
    }
}
