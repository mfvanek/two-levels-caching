/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.builders;

import com.mfvanek.caching.impl.SimpleInMemoryCache;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CacheBuilderTest {

    @Test
    void shouldReturnValueOnNullType() {
        final var builder = CacheBuilder.builder(null);
        assertThat(builder.build())
                .isNotNull()
                .isInstanceOf(SimpleInMemoryCache.class);
    }
}
