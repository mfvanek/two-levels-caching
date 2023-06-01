/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.interfaces;

import java.util.List;
import java.util.Map;

public interface Cache<K, V extends Cacheable<K>> extends Countable<K> {

    /**
     * Puts the element with given key in the cache.
     *
     * @param key   The key
     * @param value The element to be stored in the cache
     * @return Returns a list of pairs [key, element] evicted from the cache
     */
    List<Map.Entry<K, V>> put(K key, V value);

    /**
     * Puts the element in the cache.
     *
     * @param value The element to be stored in the cache
     * @return Returns a list of elements evicted from the cache
     */
    List<V> put(V value);

    V get(K key);

    boolean containsKey(K key);

    V remove(K key);

    void clear();

    int size();
}
