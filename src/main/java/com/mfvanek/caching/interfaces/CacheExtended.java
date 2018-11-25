/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.interfaces;

import java.util.Map;

public interface CacheExtended<KeyType, ValueType extends Cacheable<KeyType>>
        extends Cache<KeyType, ValueType>, Countable<KeyType> {

    Integer INVALID_FREQUENCY = -1;

    Map.Entry<Integer, ValueType> innerRemove(KeyType key) throws Exception;
}
