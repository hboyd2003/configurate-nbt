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

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Map;

class CompoundBinaryTagSerializerTest extends AbstractBinaryTagSerializerTest {

    private static CompoundBinaryTag deserialize(final ConfigurationNode node) throws SerializationException {
        return CompoundBinaryTagSerializer.INSTANCE.deserialize(CompoundBinaryTag.class, node);
    }

    @Nested
    class Serialization {

        @Test
        void nullTagLeavesTheNodeUntouched() throws SerializationException {
            CompoundBinaryTagSerializer.INSTANCE.serialize(CompoundBinaryTag.class, null,
                    CompoundBinaryTagSerializerTest.this.node);

            Assertions.assertTrue(CompoundBinaryTagSerializerTest.this.node.empty());
        }

        @Test
        void emptyCompoundSerializesToNoChildren() throws SerializationException {
            CompoundBinaryTagSerializer.INSTANCE.serialize(CompoundBinaryTag.class, CompoundBinaryTag.empty(),
                    CompoundBinaryTagSerializerTest.this.node);

            Assertions.assertTrue(CompoundBinaryTagSerializerTest.this.node.childrenMap().isEmpty());
        }

        @Test
        void compoundWithMultipleEntriesSerializesEachEntryByKey() throws SerializationException {
            final CompoundBinaryTag tag = CompoundBinaryTag.builder()
                    .putInt("count", 3)
                    .putString("name", "configurate-nbt")
                    .build();

            CompoundBinaryTagSerializer.INSTANCE.serialize(CompoundBinaryTag.class, tag,
                    CompoundBinaryTagSerializerTest.this.node);

            final var children = CompoundBinaryTagSerializerTest.this.node.childrenMap();
            Assertions.assertEquals(2, children.size());
            Assertions.assertEquals(3, children.get("count").getInt());
            Assertions.assertEquals("configurate-nbt", children.get("name").getString());
        }

        @Test
        void nestedCompoundSerializesRecursively() throws SerializationException {
            final CompoundBinaryTag tag = CompoundBinaryTag.builder()
                    .put("nested", CompoundBinaryTag.builder().putInt("value", 114).build())
                    .build();

            CompoundBinaryTagSerializer.INSTANCE.serialize(CompoundBinaryTag.class, tag,
                    CompoundBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(114,
                    CompoundBinaryTagSerializerTest.this.node.node("nested", "value").getInt());
        }
    }

    @Nested
    class Deserialization {

        @Test
        void emptyMapNodeDeserializesToEmptyCompound() throws SerializationException {
            CompoundBinaryTagSerializerTest.this.node.raw(Map.of());

            Assertions.assertEquals(CompoundBinaryTag.empty(), deserialize(CompoundBinaryTagSerializerTest.this.node));
        }

        @Test
        void nonMapNodeThrowsSerializationException() throws SerializationException {
            CompoundBinaryTagSerializerTest.this.node.set(114);

            Assertions.assertThrows(SerializationException.class,
                    () -> deserialize(CompoundBinaryTagSerializerTest.this.node));
        }

        @Test
        void mapNodeDeserializesEachEntryByKey() throws SerializationException {
            CompoundBinaryTagSerializerTest.this.node.node("count").set(3);
            CompoundBinaryTagSerializerTest.this.node.node("name").set("configurate-nbt");

            final CompoundBinaryTag expected = CompoundBinaryTag.builder()
                    .putInt("count", 3)
                    .putString("name", "configurate-nbt")
                    .build();

            Assertions.assertEquals(expected, deserialize(CompoundBinaryTagSerializerTest.this.node));
        }

        @Test
        void nestedMapNodeDeserializesRecursively() throws SerializationException {
            CompoundBinaryTagSerializerTest.this.node.node("nested", "value").set(114);

            final CompoundBinaryTag expected = CompoundBinaryTag.builder()
                    .put("nested", CompoundBinaryTag.builder().putInt("value", 114).build())
                    .build();

            Assertions.assertEquals(expected, deserialize(CompoundBinaryTagSerializerTest.this.node));
        }
    }

    @Nested
    class RoundTrip {

        @Test
        void compoundDoesNotChangeAfterRoundTrip() throws SerializationException {
            final CompoundBinaryTag tag = CompoundBinaryTag.builder()
                    .putInt("count", 3)
                    .putString("name", "configurate-nbt")
                    .put("list", ListBinaryTag.builder()
                            .add(StringBinaryTag.stringBinaryTag("one"))
                            .add(StringBinaryTag.stringBinaryTag("two"))
                            .build())
                    .build();

            CompoundBinaryTagSerializer.INSTANCE.serialize(CompoundBinaryTag.class, tag,
                    CompoundBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag, deserialize(CompoundBinaryTagSerializerTest.this.node));
        }

        @Test
        void nestedCompoundDoesNotChangeAfterRoundTrip() throws SerializationException {
            final CompoundBinaryTag tag = CompoundBinaryTag.builder()
                    .put("nested", CompoundBinaryTag.builder()
                            .putString("value", "hello")
                            .build())
                    .build();

            CompoundBinaryTagSerializer.INSTANCE.serialize(CompoundBinaryTag.class, tag,
                    CompoundBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag, deserialize(CompoundBinaryTagSerializerTest.this.node));
        }
    }
}
