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

package dev.hboyd.configurate_nbt.serializer;

import net.kyori.adventure.nbt.ArrayBinaryTag;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.IntArrayBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

class ArrayBinaryTagSerializerTest extends AbstractBinaryTagSerializerTest {

    private static ArrayBinaryTag deserialize(final ConfigurationNode node) throws SerializationException {
        return ArrayBinaryTagSerializer.INSTANCE.deserialize(ArrayBinaryTag.class, node);
    }

    @Nested
    class Serialization {

        @Test
        void nullTagLeavesTheNodeUntouched() throws SerializationException {
            ArrayBinaryTagSerializer.INSTANCE.serialize(ArrayBinaryTag.class,
                    null,
                    ArrayBinaryTagSerializerTest.this.node);

            Assertions.assertTrue(ArrayBinaryTagSerializerTest.this.node.empty());
        }

        @Test
        void intArrayBinaryTagSerializesToIntArray() throws SerializationException {
            final IntArrayBinaryTag tag = IntArrayBinaryTag.intArrayBinaryTag(1, 2, 3);

            ArrayBinaryTagSerializer.INSTANCE.serialize(ArrayBinaryTag.class,
                    tag,
                    ArrayBinaryTagSerializerTest.this.node);

            Assertions.assertArrayEquals(new int[]{1, 2, 3}, ArrayBinaryTagSerializerTest.this.node.get(int[].class));
        }

        @Test
        void byteArrayBinaryTagSerializesToByteArray() throws SerializationException {
            final ByteArrayBinaryTag tag = ByteArrayBinaryTag.byteArrayBinaryTag((byte) 1, (byte) 2, (byte) 3);

            ArrayBinaryTagSerializer.INSTANCE.serialize(ArrayBinaryTag.class,
                    tag,
                    ArrayBinaryTagSerializerTest.this.node);

            Assertions.assertArrayEquals(new byte[]{1, 2, 3}, ArrayBinaryTagSerializerTest.this.node.get(byte[].class));
        }

        @Test
        void longArrayBinaryTagSerializesToLongArray() throws SerializationException {
            final LongArrayBinaryTag tag = LongArrayBinaryTag.longArrayBinaryTag(1L, 2L, 3L);

            ArrayBinaryTagSerializer.INSTANCE.serialize(ArrayBinaryTag.class,
                    tag,
                    ArrayBinaryTagSerializerTest.this.node);

            Assertions.assertArrayEquals(new long[]{1L, 2L, 3L},
                    ArrayBinaryTagSerializerTest.this.node.get(long[].class));
        }
    }

    @Nested
    class Deserialization {

        @Test
        void intListDeserializesToIntArrayBinaryTag() throws SerializationException {
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set(1);
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set(2);
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set(3);

            Assertions.assertEquals(IntArrayBinaryTag.intArrayBinaryTag(1, 2, 3),
                    deserialize(ArrayBinaryTagSerializerTest.this.node));
        }

        @Test
        void byteListDeserializesToByteArrayBinaryTag() throws SerializationException {
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set((byte) 1);
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set((byte) 2);
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set((byte) 3);

            Assertions.assertEquals(ByteArrayBinaryTag.byteArrayBinaryTag((byte) 1, (byte) 2, (byte) 3),
                    deserialize(ArrayBinaryTagSerializerTest.this.node));
        }

        @Test
        void longListDeserializesToLongArrayBinaryTag() throws SerializationException {
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set(1L);
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set(2L);
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set(3L);

            Assertions.assertEquals(LongArrayBinaryTag.longArrayBinaryTag(1L, 2L, 3L),
                    deserialize(ArrayBinaryTagSerializerTest.this.node));
        }

        @Test
        void emptyNodeThrowsSerializationException() {
            Assertions.assertThrows(SerializationException.class,
                    () -> deserialize(ArrayBinaryTagSerializerTest.this.node));
        }

        @Test
        void unsupportedElementTypeThrowsSerializationException() throws SerializationException {
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set("one");
            ArrayBinaryTagSerializerTest.this.node.appendListNode().set("two");

            Assertions.assertThrows(SerializationException.class,
                    () -> deserialize(ArrayBinaryTagSerializerTest.this.node));
        }
    }

    @Nested
    class RoundTrip {

        @Test
        void intArrayBinaryTagDoesNotChangeWithRoundTrip() throws SerializationException {
            final IntArrayBinaryTag tag = IntArrayBinaryTag.intArrayBinaryTag(1, 2, 3);

            ArrayBinaryTagSerializer.INSTANCE.serialize(ArrayBinaryTag.class,
                    tag,
                    ArrayBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag, deserialize(ArrayBinaryTagSerializerTest.this.node));
        }

        @Test
        void byteArrayBinaryTagDoesNotChangeWithRoundTrip() throws SerializationException {
            final ByteArrayBinaryTag tag = ByteArrayBinaryTag.byteArrayBinaryTag((byte) 1, (byte) 2, (byte) 3);

            ArrayBinaryTagSerializer.INSTANCE.serialize(ArrayBinaryTag.class,
                    tag,
                    ArrayBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag, deserialize(ArrayBinaryTagSerializerTest.this.node));
        }

        @Test
        void longArrayBinaryTagDoesNotChangeAfterRoundTrip() throws SerializationException {
            final LongArrayBinaryTag tag = LongArrayBinaryTag.longArrayBinaryTag(1L, 2L, 3L);

            ArrayBinaryTagSerializer.INSTANCE.serialize(ArrayBinaryTag.class,
                    tag,
                    ArrayBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag, deserialize(ArrayBinaryTagSerializerTest.this.node));
        }
    }
}
