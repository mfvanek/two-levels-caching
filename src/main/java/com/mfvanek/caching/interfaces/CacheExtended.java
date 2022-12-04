/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.interfaces;

import java.util.Map;

public interface CacheExtended<K, V extends Cacheable<K>> extends Cache<K, V>, Countable<K> {

    Integer INVALID_FREQUENCY = -1;

    Map.Entry<Integer, V> innerRemove(K key);
}
