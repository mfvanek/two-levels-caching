/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.builders;

import io.github.mfvanek.caching.impl.SimpleInMemoryCache;
import io.github.mfvanek.caching.interfaces.Cacheable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CacheBuilderTest {

    @Test
    void shouldReturnValueOnNullType() {
        final CacheBuilder<Object, ? extends Cacheable<Object>> builder = CacheBuilder.builder(null);
        assertThat(builder.build())
                .isNotNull()
                .isInstanceOf(SimpleInMemoryCache.class);
    }
}
