/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.models;

import com.mfvanek.caching.interfaces.Cacheable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Movie implements Cacheable<String>, Serializable {

    private static final long serialVersionUID = 7524472395622976117L;

    private final String title;
    private final int year;
    private final String imdb;

    @Override
    public String getIdentifier() {
        return imdb;
    }
}
