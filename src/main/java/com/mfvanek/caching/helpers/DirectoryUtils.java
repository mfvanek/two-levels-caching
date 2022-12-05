/*
 * Copyright (c) 2018-2022. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package com.mfvanek.caching.helpers;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@UtilityClass
public final class DirectoryUtils {

    @SneakyThrows
    public static void deleteDirectory(final Path directoryToDelete) {
        if (Files.exists(directoryToDelete) && Files.isDirectory(directoryToDelete)) {
            Files.walk(directoryToDelete)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
