/*
 * Copyright (c) 2018. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.caching.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    public static Movie getMartian() {
        return new Movie("The Martian", 2015, MARTIAN_IMDB);
    }

    public static Movie getRandomGeneratedMovie() {
        final int year = new Random().nextInt(100) + 1930;
        return new Movie(UUID.randomUUID().toString(), year, UUID.randomUUID().toString());
    }

    public static List<Movie> getAllMovies() {
        return Arrays.asList(getArrival(), getAquaman(),getInception(), getInterstellar(), getMartian(), getSnowden());
    }

    public static List<Movie> getRandomGeneratedMovies(int count) {
        final List<Movie> lst = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) {
            lst.add(getRandomGeneratedMovie());
        }
        return lst;
    }

    public static final String SNOWDEN_IMDB = "tt3774114";
    public static final String AQUAMAN_IMDB = "tt1477834";
    public static final String INCEPTION_IMDB = "tt1375666";
    public static final String INTERSTELLAR_IMDB = "tt0816692";
    public static final String ARRIVAL_IMDB = "tt2543164";
    public static final String MARTIAN_IMDB = "tt3659388";
}
