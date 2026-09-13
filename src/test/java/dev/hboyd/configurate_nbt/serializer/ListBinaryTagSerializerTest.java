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

import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;

class ListBinaryTagSerializerTest extends AbstractBinaryTagSerializerTest {

    private static ListBinaryTag deserialize(final ConfigurationNode node) throws SerializationException {
        return ListBinaryTagSerializer.INSTANCE.deserialize(ListBinaryTag.class, node);
    }

    @Nested
    class Serialization {

        @Test
        void nullTagLeavesTheNodeUntouched() throws SerializationException {
            ListBinaryTagSerializer.INSTANCE.serialize(ListBinaryTag.class, null, ListBinaryTagSerializerTest.this.node);

            Assertions.assertTrue(ListBinaryTagSerializerTest.this.node.empty());
        }

        @Test
        void emptyListSerializesToNoChildren() throws SerializationException {
            ListBinaryTagSerializer.INSTANCE.serialize(ListBinaryTag.class, ListBinaryTag.empty(),
                    ListBinaryTagSerializerTest.this.node);

            Assertions.assertTrue(ListBinaryTagSerializerTest.this.node.childrenList().isEmpty());
        }

        @Test
        void homogeneousListSerializesEachElementInOrder() throws SerializationException {
            final ListBinaryTag tag = ListBinaryTag.builder(BinaryTagTypes.INT)
                    .add(IntBinaryTag.intBinaryTag(1))
                    .add(IntBinaryTag.intBinaryTag(2))
                    .add(IntBinaryTag.intBinaryTag(3))
                    .build();

            ListBinaryTagSerializer.INSTANCE.serialize(ListBinaryTag.class, tag, ListBinaryTagSerializerTest.this.node);

            final var children = ListBinaryTagSerializerTest.this.node.childrenList();
            Assertions.assertEquals(3, children.size());
            Assertions.assertEquals(1, children.get(0).getInt());
            Assertions.assertEquals(2, children.get(1).getInt());
            Assertions.assertEquals(3, children.get(2).getInt());
        }

        @Test
        void heterogeneousListSerializesEachElementInOrder() throws SerializationException {
            final ListBinaryTag tag = ListBinaryTag.heterogeneousListBinaryTag()
                    .add(IntBinaryTag.intBinaryTag(1))
                    .add(StringBinaryTag.stringBinaryTag("two"))
                    .build();

            ListBinaryTagSerializer.INSTANCE.serialize(ListBinaryTag.class, tag, ListBinaryTagSerializerTest.this.node);

            final var children = ListBinaryTagSerializerTest.this.node.childrenList();
            Assertions.assertEquals(2, children.size());
            Assertions.assertEquals(1, children.get(0).getInt());
            Assertions.assertEquals("two", children.get(1).getString());
        }
    }

    @Nested
    class Deserialization {

        @Test
        void emptyNodeDeserializesToEmptyList() throws SerializationException {
            Assertions.assertEquals(ListBinaryTag.empty(), deserialize(ListBinaryTagSerializerTest.this.node));
        }

        @Test
        void homogeneousListDeserializesToHomogeneousList() throws SerializationException {
            ListBinaryTagSerializerTest.this.node.appendListNode().set(1);
            ListBinaryTagSerializerTest.this.node.appendListNode().set(2);
            ListBinaryTagSerializerTest.this.node.appendListNode().set(3);

            final ListBinaryTag expected = ListBinaryTag.builder(BinaryTagTypes.INT)
                    .add(IntBinaryTag.intBinaryTag(1))
                    .add(IntBinaryTag.intBinaryTag(2))
                    .add(IntBinaryTag.intBinaryTag(3))
                    .build();

            Assertions.assertEquals(expected, deserialize(ListBinaryTagSerializerTest.this.node));
        }

        @Test
        void heterogeneousListDeserializesToHeterogeneousList() throws SerializationException {
            ListBinaryTagSerializerTest.this.node.appendListNode().set(1);
            ListBinaryTagSerializerTest.this.node.appendListNode().set("two");

            final ListBinaryTag expected = ListBinaryTag.heterogeneousListBinaryTag()
                    .add(IntBinaryTag.intBinaryTag(1))
                    .add(StringBinaryTag.stringBinaryTag("two"))
                    .build();

            Assertions.assertEquals(expected, deserialize(ListBinaryTagSerializerTest.this.node));
        }

        @Test
        void mixedNumberTypesDeserializeToAHeterogeneousList() throws SerializationException {
            ListBinaryTagSerializerTest.this.node.appendListNode().set(1);
            ListBinaryTagSerializerTest.this.node.appendListNode().set(2.5);

            final ListBinaryTag expected = ListBinaryTag.heterogeneousListBinaryTag()
                    .add(IntBinaryTag.intBinaryTag(1))
                    .add(DoubleBinaryTag.doubleBinaryTag(2.5))
                    .build();

            Assertions.assertEquals(expected, deserialize(ListBinaryTagSerializerTest.this.node));
        }
    }

    @Nested
    class RoundTrip {

        @Test
        void homogeneousListSurvivesARoundTrip() throws SerializationException {
            final ListBinaryTag tag = ListBinaryTag.builder(BinaryTagTypes.STRING)
                    .add(StringBinaryTag.stringBinaryTag("one"))
                    .add(StringBinaryTag.stringBinaryTag("two"))
                    .build();

            ListBinaryTagSerializer.INSTANCE.serialize(ListBinaryTag.class, tag, ListBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag, deserialize(ListBinaryTagSerializerTest.this.node));
        }

        @Test
        void heterogeneousListDoesNotChangeAfterRoundTrip() throws SerializationException {
            final ListBinaryTag tag = ListBinaryTag.heterogeneousListBinaryTag()
                    .add(IntBinaryTag.intBinaryTag(114))
                    .add(StringBinaryTag.stringBinaryTag("value"))
                    .build();

            ListBinaryTagSerializer.INSTANCE.serialize(ListBinaryTag.class, tag, ListBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag, deserialize(ListBinaryTagSerializerTest.this.node));
        }

        @Test
        void heterogeneousListWithHomogeneousContentsBecomesHomogeneousAfterRoundTrip() throws SerializationException {
            final ListBinaryTag tag = ListBinaryTag.heterogeneousListBinaryTag()
                    .add(IntBinaryTag.intBinaryTag(1))
                    .add(IntBinaryTag.intBinaryTag(2))
                    .build();

            // The original tag still permits heterogeneous elements, despite only containing ints so far.
            Assertions.assertDoesNotThrow(() -> tag.add(StringBinaryTag.stringBinaryTag("three")));

            ListBinaryTagSerializer.INSTANCE.serialize(ListBinaryTag.class, tag, ListBinaryTagSerializerTest.this.node);
            final ListBinaryTag roundTripped = deserialize(ListBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag, roundTripped);
            Assertions.assertThrows(IllegalArgumentException.class, () -> roundTripped.add(StringBinaryTag.stringBinaryTag("three")));
        }
    }

    @Test
    void emptyValueIsAnEmptyList() {
        Assertions.assertEquals(ListBinaryTag.empty(),
                ListBinaryTagSerializer.INSTANCE.emptyValue(ListBinaryTag.class, ConfigurationOptions.defaults()));
    }
}
