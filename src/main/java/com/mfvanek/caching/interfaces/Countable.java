/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.interfaces;

public interface Countable<K> {

    int frequencyOf(K key);

    int getLowestFrequency();
}
