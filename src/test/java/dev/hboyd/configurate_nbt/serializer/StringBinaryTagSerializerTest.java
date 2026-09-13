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

import net.kyori.adventure.nbt.StringBinaryTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.spongepowered.configurate.serialize.SerializationException;

class StringBinaryTagSerializerTest extends AbstractBinaryTagSerializerTest {

    @Nested
    class Serialization {

        @Test
        void nullTagLeavesTheNodeUntouched() {
            StringBinaryTagSerializer.INSTANCE.serialize(StringBinaryTag.class, null,
                    StringBinaryTagSerializerTest.this.node);

            Assertions.assertTrue(StringBinaryTagSerializerTest.this.node.empty());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "hello", "with spaces", "line1\nline2", "unicode éè"})
        void stringBinaryTagSerializesToString(final String value) {
            final String serialized = (String) StringBinaryTagSerializer.INSTANCE
                    .serialize(StringBinaryTag.stringBinaryTag(value), _ -> true);

            Assertions.assertEquals(value, serialized);
        }
    }

    @Nested
    class Deserialization {

        @ParameterizedTest
        @ValueSource(strings = {"", "hello", "with spaces", "line1\nline2", "unicode éè"})
        void stringDeserializesToStringBinaryTag(final String value) throws SerializationException {
            final StringBinaryTag deserialized = StringBinaryTagSerializer.INSTANCE.deserialize(value);

            Assertions.assertEquals(StringBinaryTag.stringBinaryTag(value), deserialized);
        }

        @Test
        void nonStringValueDeserializesUsingItsStringRepresentation() throws SerializationException {
            final StringBinaryTag deserialized = StringBinaryTagSerializer.INSTANCE.deserialize(114);

            Assertions.assertEquals(StringBinaryTag.stringBinaryTag("114"), deserialized);
        }
    }

    @Nested
    class RoundTrip {

        @Test
        void stringBinaryTagDoesNotChangeAfterRoundTrip() throws SerializationException {
            final StringBinaryTag tag = StringBinaryTag.stringBinaryTag("configurate-nbt");

            StringBinaryTagSerializer.INSTANCE.serialize(StringBinaryTag.class, tag,
                    StringBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag,
                    StringBinaryTagSerializer.INSTANCE.deserialize(StringBinaryTag.class,
                            StringBinaryTagSerializerTest.this.node));
        }

        @Test
        void emptyStringBinaryTagDoesNotChangeAfterRoundTrip() throws SerializationException {
            final StringBinaryTag tag = StringBinaryTag.stringBinaryTag("");

            StringBinaryTagSerializer.INSTANCE.serialize(StringBinaryTag.class, tag,
                    StringBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag,
                    StringBinaryTagSerializer.INSTANCE.deserialize(StringBinaryTag.class,
                            StringBinaryTagSerializerTest.this.node));
        }
    }
}
