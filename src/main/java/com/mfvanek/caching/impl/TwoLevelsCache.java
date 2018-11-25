/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.interfaces.CacheExtended;
import com.mfvanek.caching.interfaces.Cacheable;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TwoLevelsCache<KeyType, ValueType extends Cacheable<KeyType> & Serializable>
        implements Cache<KeyType, ValueType> {

    private static final Logger logger = LoggerFactory.getLogger(TwoLevelsCache.class);

    private final CacheExtended<KeyType, ValueType> firstLevel;
    private final CacheExtended<KeyType, ValueType> secondLevel;

    public TwoLevelsCache(CacheExtended<KeyType, ValueType> firstLevel, CacheExtended<KeyType, ValueType> secondLevel) {
        this.firstLevel = firstLevel;
        this.secondLevel = secondLevel;
    }

    @Override
    public List<Map.Entry<KeyType, ValueType>> put(KeyType key, ValueType value) throws Exception {
        List<Map.Entry<KeyType, ValueType>> evictedItems;
        // If the item is already in the cache and stored in the second level,
        // we will not move it up, just update the value.
        if (secondLevel.containsKey(key)) {
            logger.trace("The item is already in the cache and stored in the second level");
            evictedItems = secondLevel.put(key, value);
        } else {
            // The item that is not present in the cache or is held on the first level, will be proceeded as usual.
            final List<Map.Entry<KeyType, ValueType>> firstLevelEvictedItems = firstLevel.put(key, value);
            if (CollectionUtils.isNotEmpty(firstLevelEvictedItems)) {
                // TODO implement Cache::putAll method
                evictedItems = new LinkedList<>();
                for (Map.Entry<KeyType, ValueType> entry : firstLevelEvictedItems) {
                    final List<Map.Entry<KeyType, ValueType>> secondLevelEvictedItems = secondLevel.put(entry.getKey(), entry.getValue());
                    evictedItems.addAll(secondLevelEvictedItems);
                }
            } else {
                evictedItems = firstLevelEvictedItems;
            }
        }
        logger.debug("evictedItems = {}", evictedItems);
        return evictedItems;
    }

    @Override
    public List<ValueType> put(ValueType value) throws Exception {
        final List<Map.Entry<KeyType, ValueType>> evictedItems =  this.put(value.getIdentifier(), value);
        return evictedItems.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public ValueType get(KeyType key) throws Exception {
        // TODO we need to refresh the cache on getting values
        ValueType foundItem = firstLevel.get(key);
        if (foundItem == null) {
            foundItem = secondLevel.get(key);
        }
        return foundItem;
    }

    @Override
    public boolean containsKey(KeyType key) {
        if (firstLevel.containsKey(key)) {
            logger.trace("The item is in the cache and stored in the first level");
            return true;
        }

        if (secondLevel.containsKey(key)) {
            logger.trace("The item is in the cache and stored in the second level");
            return true;
        }

        logger.trace("The item doesn't present in the cache");
        return false;
    }

    @Override
    public ValueType remove(KeyType key) throws Exception {
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
            logger.trace("The item has been successfully deleted from the {} level; {}", level, deletedItem);
        } else {
            logger.trace("No item has been deleted");
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
