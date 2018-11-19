package com.mfvanek.caching.interfaces;

public interface Cacheable<KeyType> {

    KeyType getIdentifier();
}
