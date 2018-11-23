/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.impl;

import com.mfvanek.caching.builders.TwoLevelsCacheBuilder;
import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;

class TwoLevelsCacheTest extends BaseCacheTest {

    @Override
    protected Cache<String, Movie> createCache() throws Exception {
        return createCache(MAX_SIZE);
    }

    @Override
    protected Cache<String, Movie> createCache(int maxSize) throws Exception {
        return TwoLevelsCacheBuilder.getInstance(Movie.class).setFirstLevelMaxSize(maxSize).setSecondLevelMaxSize(maxSize).build();
    }
}