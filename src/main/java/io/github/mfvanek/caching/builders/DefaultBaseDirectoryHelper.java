/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.builders;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
class DefaultBaseDirectoryHelper {

    static Path forMacOs() {
        return Paths.get(System.getProperty("user.home"), "Library/Caches", "jcache")
                .toAbsolutePath();
    }

    static Path forLinux() {
        return Paths.get("/var/tmp/", "jcache")
                .toAbsolutePath();
    }

    static Path forWindows() {
        return Paths.get(".")
                .resolve("/jcache/")
                .toAbsolutePath();
    }
}
