/*
 * configurate-nbt
 * Copyright (c) 2026 Harrison Boyd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.hboyd.configurateNBT.serializer;

import net.kyori.adventure.nbt.ArrayBinaryTag;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.IntArrayBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Serializer for {@link ArrayBinaryTag}s.
 */
public final class ArrayBinaryTagSerializer implements TypeSerializer<ArrayBinaryTag> {
    public static final ArrayBinaryTagSerializer INSTANCE = new ArrayBinaryTagSerializer();

    private ArrayBinaryTagSerializer() {}

    @Override
    public ArrayBinaryTag deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        final ListBinaryTag listBinaryTag = node.options().serializers().get(ListBinaryTag.class).deserialize(ListBinaryTag.class, node);
        listBinaryTag.unwrapHeterogeneity();
        if (listBinaryTag.elementType() == BinaryTagTypes.INT) {
            final int[] intArray = new int[listBinaryTag.size()];
            for (int i = 0; i < intArray.length; i++) {
                intArray[i] = listBinaryTag.getInt(i);
            }
            return IntArrayBinaryTag.intArrayBinaryTag(intArray);
        } else if (listBinaryTag.elementType() == BinaryTagTypes.BYTE) {
            final byte[] byteArray = new byte[listBinaryTag.size()];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = listBinaryTag.getByte(i);
            }
            return ByteArrayBinaryTag.byteArrayBinaryTag(byteArray);
        } else if (listBinaryTag.elementType() == BinaryTagTypes.LONG) {
            final long[] longArray = new long[listBinaryTag.size()];
            for (int i = 0; i < longArray.length; i++) {
                longArray[i] = listBinaryTag.getLong(i);
            }
            return LongArrayBinaryTag.longArrayBinaryTag(longArray);
        } else throw new SerializationException("Unknown array binary tag will element type of " + listBinaryTag.elementType());
    }

    @Override
    public void serialize(final Type type, @Nullable final ArrayBinaryTag arrayBinaryTag, final ConfigurationNode node) throws SerializationException {
        if (arrayBinaryTag == null) return;
        switch (arrayBinaryTag) {
            case final IntArrayBinaryTag intArrayBinaryTag -> node.set(int[].class, intArrayBinaryTag.value());
            case final ByteArrayBinaryTag intArrayBinaryTag -> node.set(byte[].class, intArrayBinaryTag.value());
            case final LongArrayBinaryTag longArrayBinaryTag -> node.set(long[].class, longArrayBinaryTag.value());
            default -> throw new IllegalStateException("Unknown array binary tag type: " + arrayBinaryTag);
        }
    }
}
