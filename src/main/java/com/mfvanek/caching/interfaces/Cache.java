/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.interfaces;

import java.util.List;
import java.util.Map;

public interface Cache<KeyType, ValueType extends Cacheable<KeyType>> {

    /**
     * Puts the element with given key in the cache
     * @param key The key
     * @param value The element to be stored in the cache
     * @return Returns a list of pairs [key, element] evicted from the cache
     */
    List<Map.Entry<KeyType, ValueType>> put(KeyType key, ValueType value) throws Exception;

    /**
     * Puts the element in the cache
     * @param value The element to be stored in the cache
     * @return Returns a list of elements evicted from the cache
     */
    List<ValueType> put(ValueType value) throws Exception;

    ValueType get(KeyType key) throws Exception;

    boolean containsKey(KeyType key);

    ValueType remove(KeyType key) throws Exception;

    void clear() throws Exception;

    int size();
}
