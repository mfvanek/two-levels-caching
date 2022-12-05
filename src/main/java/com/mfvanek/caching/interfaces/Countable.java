/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.interfaces;

public interface Countable<K> {

    int frequencyOf(K key);

    int getLowestFrequency();
}
