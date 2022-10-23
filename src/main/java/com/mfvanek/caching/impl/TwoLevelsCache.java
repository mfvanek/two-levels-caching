/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.CacheExtended;
import com.mfvanek.caching.interfaces.Cacheable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class TwoLevelsCache<KeyType, ValueType extends Cacheable<KeyType> & Serializable>
        implements Cache<KeyType, ValueType> {

    private final CacheExtended<KeyType, ValueType> firstLevel;
    private final CacheExtended<KeyType, ValueType> secondLevel;

    public TwoLevelsCache(CacheExtended<KeyType, ValueType> firstLevel, CacheExtended<KeyType, ValueType> secondLevel) {
        this.firstLevel = firstLevel;
        this.secondLevel = secondLevel;
    }

    @Override
    public List<Map.Entry<KeyType, ValueType>> put(KeyType key, ValueType value) {
        List<Map.Entry<KeyType, ValueType>> evictedItems;
        // If the item is already in the cache and stored in the second level,
        // we will not move it up, just update the value.
        if (secondLevel.containsKey(key)) {
            log.trace("The item is already in the cache and stored in the second level");
            evictedItems = secondLevel.put(key, value);
        } else {
            // The item that is not present in the cache or is held on the first level, will be proceeded as usual.
            final List<Map.Entry<KeyType, ValueType>> firstLevelEvictedItems = firstLevel.put(key, value);
            if (CollectionUtils.isNotEmpty(firstLevelEvictedItems)) {
                log.trace("Some elements have been evicted from the first level = {}", firstLevelEvictedItems);
                // TODO implement Cache::putAll method
                evictedItems = new LinkedList<>();
                for (Map.Entry<KeyType, ValueType> entry : firstLevelEvictedItems) {
                    final List<Map.Entry<KeyType, ValueType>> secondLevelEvictedItems = secondLevel.put(entry.getKey(), entry.getValue());
                    evictedItems.addAll(secondLevelEvictedItems);
                }
            } else {
                log.trace("None of the elements have been evicted from the first level");
                evictedItems = firstLevelEvictedItems;
            }
        }
        log.debug("evictedItems = {}", evictedItems);
        return evictedItems;
    }

    @Override
    public List<ValueType> put(ValueType value) {
        final List<Map.Entry<KeyType, ValueType>> evictedItems =  this.put(value.getIdentifier(), value);
        return evictedItems.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public ValueType get(KeyType key) {
        String level = null;
        // TODO we need to refresh the cache on getting values
        ValueType foundItem = firstLevel.get(key);
        if (foundItem == null) {
            foundItem = secondLevel.get(key);
            if (foundItem != null) {
                level = "second";
            }
        } else {
            level = "first";
        }
        if (level != null) {
            log.trace("The item has been found in the {} level; {}", level, foundItem);
        } else {
            log.trace("The item with key = {} hasn't been found in the cache", key);
        }
        return foundItem;
    }

    @Override
    public boolean containsKey(KeyType key) {
        boolean result;
        String level = null;
        if ((result = firstLevel.containsKey(key))) {
            level = "first";
        } else {
            if ((result = secondLevel.containsKey(key))) {
                level = "second";
            }
        }

        if (level != null) {
            log.trace("The item with key = {} is in the cache and stored in the {} level", key, level);
        } else {
            log.trace("The item with key = {} doesn't present in the cache", key);
        }
        return result;
    }

    @Override
    public ValueType remove(KeyType key) {
        String level = null;
        ValueType deletedItem = firstLevel.remove(key);
        if (deletedItem == null) {
            deletedItem = secondLevel.remove(key);
            if (deletedItem != null) {
                level = "second";
            }
        } else {
            level = "first";
        }
        if (level != null) {
            log.trace("The item has been successfully deleted from the {} level; {}", level, deletedItem);
        } else {
            log.trace("No item has been deleted with key = {}", key);
        }
        return deletedItem;
    }

    @Override
    public void clear() {
        firstLevel.clear();
        secondLevel.clear();
    }

    @Override
    public int size() {
        return firstLevel.size() + secondLevel.size();
    }
}
