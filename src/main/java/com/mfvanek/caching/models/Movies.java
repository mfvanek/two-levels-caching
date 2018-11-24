/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.models;

public class Movies {

    private Movies() {}

    public static Movie getSnowden() {
        return new Movie("Snowden", 2016, SNOWDEN_IMDB);
    }

    public static Movie getAquaman() {
        return new Movie("Aquaman", 2018, AQUAMAN_IMDB);
    }

    public static Movie getInception() {
        return new Movie("Inception", 2010, INCEPTION_IMDB);
    }

    public static Movie getInterstellar() {
        return new Movie("Interstellar", 2016, INTERSTELLAR_IMDB);
    }

    public static Movie getArrival() {
        return new Movie("Arrival", 2016, ARRIVAL_IMDB);
    }

    public static final String SNOWDEN_IMDB = "tt3774114";
    public static final String AQUAMAN_IMDB = "tt1477834";
    public static final String INCEPTION_IMDB = "tt1375666";
    public static final String INTERSTELLAR_IMDB = "tt0816692";
    public static final String ARRIVAL_IMDB = "tt2543164";
}
