/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.models;

import com.mfvanek.caching.interfaces.Cacheable;

import java.io.Serial;
import java.io.Serializable;

public record Movie(String title, int year, String imdb) implements Cacheable<String>, Serializable {

    @Serial
    private static final long serialVersionUID = 7524472395622976117L;

    @Override
    public String getIdentifier() {
        return imdb;
    }
}
