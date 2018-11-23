/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.helpers;

import com.mfvanek.caching.interfaces.Cache;
import com.mfvanek.caching.models.Movie;
import com.mfvanek.caching.models.Movies;

public abstract class BaseCacheTest {

    protected static final int MAX_SIZE = 2;
    protected static final Movie SNOWDEN = Movies.getSnowden();
    protected static final Movie AQUAMAN = Movies.getAquaman();
    protected static final Movie INCEPTION = Movies.getInception();

    protected abstract Cache<String, Movie> createCache() throws Exception;
}
