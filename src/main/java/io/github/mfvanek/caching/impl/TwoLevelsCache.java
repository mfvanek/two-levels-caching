/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.impl;

import io.github.mfvanek.caching.interfaces.Cacheable;
import io.github.mfvanek.caching.interfaces.LeveledCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class TwoLevelsCache<K, V extends Cacheable<K> & Serializable> implements LeveledCache<K, V> {

    private final LeveledCache<K, V> firstLevel;
    private final LeveledCache<K, V> secondLevel;

    public TwoLevelsCache(final LeveledCache<K, V> firstLevel,
                          final LeveledCache<K, V> secondLevel) {
        this.firstLevel = firstLevel;
        this.secondLevel = secondLevel;
    }

    @Override
    public List<Map.Entry<K, V>> put(final K key, final V value) {
        final List<Map.Entry<K, V>> evictedItems;
        // If the item is already in the cache and stored in the second level,
        // we will not move it up, just update the value.
        if (secondLevel.containsKey(key)) {
            log.trace("The item is already in the cache and stored in the second level");
            evictedItems = secondLevel.put(key, value);
        } else {
            // The item that is not present in the cache or is held on the first level, will be proceeded as usual.
            final List<Map.Entry<K, V>> firstLevelEvictedItems = firstLevel.put(key, value);
            if (CollectionUtils.isNotEmpty(firstLevelEvictedItems)) {
                log.trace("Some elements have been evicted from the first level = {}", firstLevelEvictedItems);
                evictedItems = new ArrayList<>();
                for (final Map.Entry<K, V> entry : firstLevelEvictedItems) {
                    final List<Map.Entry<K, V>> secondLevelEvictedItems = secondLevel.put(entry.getKey(), entry.getValue());
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
    public List<V> put(final V value) {
        final List<Map.Entry<K, V>> evictedItems = this.put(value.getIdentifier(), value);
        return evictedItems.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public V get(final K key) {
        String level = null;
        // TODO we need to refresh the cache on getting values
        V foundItem = firstLevel.get(key);
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
    public boolean containsKey(final K key) {
        final String level;
        boolean result = firstLevel.containsKey(key);
        if (result) {
            level = "first";
        } else {
            result = secondLevel.containsKey(key);
            if (result) {
                level = "second";
            } else {
                level = null;
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
    public V remove(final K key) {
        String level = null;
        V deletedItem = firstLevel.remove(key);
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

    @Override
    public int frequencyOf(final K key) {
        return firstLevel.frequencyOf(key) + secondLevel.frequencyOf(key);
    }

    @Override
    public int getLowestFrequency() {
        return Math.min(firstLevel.getLowestFrequency(), secondLevel.getLowestFrequency());
    }
}
