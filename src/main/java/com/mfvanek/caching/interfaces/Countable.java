/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.interfaces;

import java.util.NoSuchElementException;

public interface Countable<KeyType> {

    int frequencyOf(KeyType key) throws NoSuchElementException;

    int getLowestFrequency();
}
