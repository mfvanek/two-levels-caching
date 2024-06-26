/*
 * Copyright (c) 2018-2023. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek/two-levels-caching
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.caching.helpers;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@UtilityClass
public final class CacheSerializer {

    @SneakyThrows
    public static <V extends Serializable> Path serialize(final V value, final Path cacheFilePath) {
        try (FileChannel channel = FileChannel.open(cacheFilePath, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream ous = new ObjectOutputStream(bos)) {
            ous.writeObject(value);
            channel.write(ByteBuffer.wrap(bos.toByteArray()));
        }
        return cacheFilePath;
    }

    @SneakyThrows
    public static <V extends Serializable> V deserialize(final Class<V> type, final Path cacheFilePath) {
        final byte[] data = Files.readAllBytes(cacheFilePath);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return type.cast(ois.readObject());
        }
    }
}
