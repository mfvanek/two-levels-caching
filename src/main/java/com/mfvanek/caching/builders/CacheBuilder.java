package com.mfvanek.caching.builders;

import com.mfvanek.caching.interfaces.Cache;

public class CacheBuilder<KeyType, ValueType>  {

    private CacheBuilder() {
    }

    public Cache<KeyType, ValueType> build() {
        return null;
    }

    public static <KeyType, ValueType> CacheBuilder<KeyType, ValueType> getInstance() {
        return new CacheBuilder<KeyType, ValueType>();
    }
}
