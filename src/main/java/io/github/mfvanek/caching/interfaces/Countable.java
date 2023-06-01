/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.interfaces;

public interface Countable<K> {

    Integer INVALID_FREQUENCY = -1;

    int frequencyOf(K key);

    int getLowestFrequency();
}
