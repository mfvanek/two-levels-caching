/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.models;

import io.github.mfvanek.caching.interfaces.Cacheable;

import java.io.Serial;
import java.io.Serializable;

public record Movie(String title, int year, String imdb) implements Cacheable<String>, Serializable {

    @Serial
    private static final long serialVersionUID = 7727284494968355657L;

    @Override
    public String getIdentifier() {
        return imdb;
    }
}
