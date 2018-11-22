/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.helpers.LFUCacheHelper;
import com.mfvanek.caching.interfaces.Cacheable;
import org.apache.commons.lang3.NotImplementedException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PersistenceLFUCache<KeyType, ValueType extends Cacheable<KeyType> & Serializable>
        extends AbstractCache<KeyType, ValueType> {

    private final LFUCacheHelper<KeyType> helper;

    public PersistenceLFUCache(int maxCacheSize, float evictionFactor) {
        super(maxCacheSize);
        helper = new LFUCacheHelper<>(evictionFactor);
    }

    @Override
    public List<Map.Entry<KeyType, ValueType>> put(KeyType key, ValueType value) {
        throw new NotImplementedException("");
    }

    @Override
    public ValueType get(KeyType key) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean containsKey(KeyType key) {
        throw new NotImplementedException("");
    }

    @Override
    public ValueType remove(KeyType key) {
        throw new NotImplementedException("");
    }

    @Override
    public void clear() {
        throw new NotImplementedException("");
    }

    @Override
    public int size() {
        throw new NotImplementedException("");
    }
}
