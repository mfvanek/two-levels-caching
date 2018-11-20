/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.interfaces;

public interface Cache<KeyType, ValueType extends Cacheable<KeyType>> {

    void put(KeyType key, ValueType value);

    void put(ValueType value);

    ValueType get(KeyType key);

    boolean containsKey(KeyType key);

    ValueType remove(KeyType key);

    void clear();

    int size();
}
