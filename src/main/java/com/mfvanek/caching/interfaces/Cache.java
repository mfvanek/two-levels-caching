package com.mfvanek.caching.interfaces;

public interface Cache<KeyType, ValueType> {

    void put(KeyType key, ValueType value);

    ValueType get(KeyType key);

    boolean containsKey(KeyType key);

    ValueType remove(KeyType key);

    void clear();
}
