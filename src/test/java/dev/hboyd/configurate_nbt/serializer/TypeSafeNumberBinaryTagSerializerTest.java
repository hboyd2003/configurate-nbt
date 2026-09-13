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

import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.NumberBinaryTag;
import net.kyori.adventure.nbt.ShortBinaryTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

class TypeSafeNumberBinaryTagSerializerTest {

    private static String serialize(final NumberBinaryTag tag) throws SerializationException {
        final ConfigurationNode node = BasicConfigurationNode.root();
        TypeSafeNumberBinaryTagSerializer.INSTANCE.serialize(NumberBinaryTag.class, tag, node);

        return node.getString();
    }

    private static NumberBinaryTag deserialize(final Object raw) throws SerializationException {
        return TypeSafeNumberBinaryTagSerializer.INSTANCE
                .deserialize(NumberBinaryTag.class, BasicConfigurationNode.root().set(raw));
    }

    @Nested
    class Serialization {

        @ParameterizedTest
        @ValueSource(ints = {0, -99, 114, Integer.MAX_VALUE, Integer.MIN_VALUE})
        void intBinaryTagSerializesWithoutASuffix(final int intValue) throws SerializationException {
            Assertions.assertEquals(Integer.toString(intValue), serialize(IntBinaryTag.intBinaryTag(intValue)));
        }

        @ParameterizedTest
        @ValueSource(doubles = {0, -99.4968, 114.8542, Double.MAX_VALUE, Double.MIN_VALUE})
        void doubleBinaryTagSerializesWithoutASuffix(final double doubleValue) throws SerializationException {
            Assertions.assertEquals(Double.toString(doubleValue), serialize(DoubleBinaryTag.doubleBinaryTag(doubleValue)));
        }

        @ParameterizedTest
        @ValueSource(bytes = {0, -99, 114, Byte.MAX_VALUE, Byte.MIN_VALUE})
        void byteBinaryTagSerializesWithAByteSuffix(final byte byteValue) throws SerializationException {
            Assertions.assertEquals(byteValue + "b", serialize(ByteBinaryTag.byteBinaryTag(byteValue)));
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -991876462, 1145461646, Long.MAX_VALUE, Long.MIN_VALUE})
        void longBinaryTagSerializesWithALongSuffix(final long longValue) throws SerializationException {
            Assertions.assertEquals(longValue + "l", serialize(LongBinaryTag.longBinaryTag(longValue)));
        }

        @ParameterizedTest
        @ValueSource(shorts = {0, -9945, 11454, Short.MAX_VALUE, Short.MIN_VALUE})
        void shortBinaryTagSerializesWithAShortSuffix(final short shortValue) throws SerializationException {
            Assertions.assertEquals(shortValue + "s", serialize(ShortBinaryTag.shortBinaryTag(shortValue)));
        }

        @ParameterizedTest
        @ValueSource(floats = {0, -99.9419841f, 114.54894f, Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN})
        void floatBinaryTagSerializesWithAFloatSuffix(final float floatValue) throws SerializationException {
            Assertions.assertEquals(Float.toString(floatValue) + "f", serialize(FloatBinaryTag.floatBinaryTag(floatValue)));
        }

        @ParameterizedTest
        @ValueSource(doubles = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
        void infiniteDoubleBinaryTagFailsToSerialize(final double doubleValue) {
            Assertions.assertThrows(SerializationException.class,
                    () -> serialize(DoubleBinaryTag.doubleBinaryTag(doubleValue)));
        }

        @ParameterizedTest
        @ValueSource(floats = {Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY})
        void infiniteFloatBinaryTagFailsToSerialize(final float floatValue) {
            Assertions.assertThrows(SerializationException.class,
                    () -> serialize(FloatBinaryTag.floatBinaryTag(floatValue)));
        }

        @Test
        void nullTagLeavesTheNodeUntouched() throws SerializationException {
            final ConfigurationNode node = BasicConfigurationNode.root();
            TypeSafeNumberBinaryTagSerializer.INSTANCE.serialize(NumberBinaryTag.class, null, node);

            Assertions.assertTrue(node.empty());
        }
    }

    @Nested
    class Deserialization {

        @ParameterizedTest
        @ValueSource(ints = {0, -99, 114, Integer.MAX_VALUE, Integer.MIN_VALUE})
        void intSuffixedStringDeserializesToIntBinaryTag(final int intValue) throws SerializationException {
            Assertions.assertEquals(IntBinaryTag.intBinaryTag(intValue), deserialize(intValue + "i"));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -99, 114, Integer.MAX_VALUE, Integer.MIN_VALUE})
        void unsuffixedWholeNumberDeserializesToIntBinaryTag(final int intValue) throws SerializationException {
            Assertions.assertEquals(IntBinaryTag.intBinaryTag(intValue), deserialize(Integer.toString(intValue)));
        }

        @ParameterizedTest
        @ValueSource(doubles = {0, -99.4968, 114.8542, Double.MAX_VALUE, Double.MIN_VALUE, Double.NaN})
        void doubleSuffixedStringDeserializesToDoubleBinaryTag(final double doubleValue) throws SerializationException {
            Assertions.assertEquals(DoubleBinaryTag.doubleBinaryTag(doubleValue), deserialize(doubleValue + "d"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {0, -99.4968, 114.8542, Double.MAX_VALUE, Double.MIN_VALUE})
        void unsuffixedDecimalDeserializesToDoubleBinaryTag(final double doubleValue) throws SerializationException {
            Assertions.assertEquals(DoubleBinaryTag.doubleBinaryTag(doubleValue), deserialize(Double.toString(doubleValue)));
        }

        @ParameterizedTest
        @ValueSource(bytes = {0, -99, 114, Byte.MAX_VALUE, Byte.MIN_VALUE})
        void byteSuffixedStringDeserializesToByteBinaryTag(final byte byteValue) throws SerializationException {
            Assertions.assertEquals(ByteBinaryTag.byteBinaryTag(byteValue), deserialize(byteValue + "b"));
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -991876462, 1145461646, Long.MAX_VALUE, Long.MIN_VALUE})
        void longSuffixedStringDeserializesToLongBinaryTag(final long longValue) throws SerializationException {
            Assertions.assertEquals(LongBinaryTag.longBinaryTag(longValue), deserialize(longValue + "l"));
        }

        @ParameterizedTest
        @ValueSource(shorts = {0, -9945, 11454, Short.MAX_VALUE, Short.MIN_VALUE})
        void shortSuffixedStringDeserializesToShortBinaryTag(final short shortValue) throws SerializationException {
            Assertions.assertEquals(ShortBinaryTag.shortBinaryTag(shortValue), deserialize(shortValue + "s"));
        }

        @ParameterizedTest
        @ValueSource(floats = {0, -99.9419841f, 114.54894f, Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN})
        void floatSuffixedStringDeserializesToFloatBinaryTag(final float floatValue) throws SerializationException {
            Assertions.assertEquals(FloatBinaryTag.floatBinaryTag(floatValue), deserialize(floatValue + "f"));
        }

        @Test
        void suffixesAreCaseInsensitive() {
            Assertions.assertAll(
                    () -> Assertions.assertEquals(IntBinaryTag.intBinaryTag(10), deserialize("10I")),
                    () -> Assertions.assertEquals(DoubleBinaryTag.doubleBinaryTag(10), deserialize("10D")),
                    () -> Assertions.assertEquals(ByteBinaryTag.byteBinaryTag((byte) 10), deserialize("10B")),
                    () -> Assertions.assertEquals(LongBinaryTag.longBinaryTag(10), deserialize("10L")),
                    () -> Assertions.assertEquals(ShortBinaryTag.shortBinaryTag((short) 10), deserialize("10S")),
                    () -> Assertions.assertEquals(FloatBinaryTag.floatBinaryTag(10), deserialize("10F")));
        }

        @Test
        void nonStringScalarsDeserializeFromTheirStringRepresentation() {
            Assertions.assertAll(
                    () -> Assertions.assertEquals(IntBinaryTag.intBinaryTag(114), deserialize(114)),
                    () -> Assertions.assertEquals(IntBinaryTag.intBinaryTag(114), deserialize((short) 114)),
                    () -> Assertions.assertEquals(DoubleBinaryTag.doubleBinaryTag(114.8542), deserialize(114.8542)));
        }

        @Test
        void emptyNodeFailsToDeserialize() {
            Assertions.assertThrows(SerializationException.class, () -> TypeSafeNumberBinaryTagSerializer.INSTANCE
                    .deserialize(NumberBinaryTag.class, BasicConfigurationNode.root()));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "", // Empty
                "z", "10z", "10.5z", // Unknown suffixes
                "i", "b", "l", "s", "f", "d", // Suffix only
                "abc", "abci", "10.5.5d", // Unparsable
                "9999999999", "9999999999i", // Int overflow
                "99999999999999999999l", // Long overflow
                "99999s", // Short overflow
                "999b", // Byte overflow
                "10.5i", "10.5b", "10.5l", "10.5s", // Decimals in integral types
        })
        void malformedStringFailsToDeserialize(final String raw) {
            Assertions.assertThrows(SerializationException.class, () -> deserialize(raw));
        }
    }

    @Nested
    class RoundTrip {

        @ParameterizedTest
        @ValueSource(ints = {0, -99, 114, Integer.MAX_VALUE, Integer.MIN_VALUE})
        void intBinaryTagSurvivesARoundTrip(final int intValue) throws SerializationException {
            final IntBinaryTag tag = IntBinaryTag.intBinaryTag(intValue);

            Assertions.assertEquals(tag, deserialize(serialize(tag)));
        }

        @ParameterizedTest
        @ValueSource(doubles = {0, -99.4968, 114.8542, Double.MAX_VALUE, Double.MIN_VALUE})
        void doubleBinaryTagSurvivesARoundTrip(final double doubleValue) throws SerializationException {
            final DoubleBinaryTag tag = DoubleBinaryTag.doubleBinaryTag(doubleValue);

            Assertions.assertEquals(tag, deserialize(serialize(tag)));
        }

        @ParameterizedTest
        @ValueSource(bytes = {0, -99, 114, Byte.MAX_VALUE, Byte.MIN_VALUE})
        void byteBinaryTagSurvivesARoundTrip(final byte byteValue) throws SerializationException {
            final ByteBinaryTag tag = ByteBinaryTag.byteBinaryTag(byteValue);

            Assertions.assertEquals(tag, deserialize(serialize(tag)));
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -991876462, 1145461646, Long.MAX_VALUE, Long.MIN_VALUE})
        void longBinaryTagSurvivesARoundTrip(final long longValue) throws SerializationException {
            final LongBinaryTag tag = LongBinaryTag.longBinaryTag(longValue);

            Assertions.assertEquals(tag, deserialize(serialize(tag)));
        }

        @ParameterizedTest
        @ValueSource(shorts = {0, -9945, 11454, Short.MAX_VALUE, Short.MIN_VALUE})
        void shortBinaryTagSurvivesARoundTrip(final short shortValue) throws SerializationException {
            final ShortBinaryTag tag = ShortBinaryTag.shortBinaryTag(shortValue);

            Assertions.assertEquals(tag, deserialize(serialize(tag)));
        }

        @ParameterizedTest
        @ValueSource(floats = {0, -99.9419841f, 114.54894f, Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN})
        void floatBinaryTagSurvivesARoundTrip(final float floatValue) throws SerializationException {
            final FloatBinaryTag tag = FloatBinaryTag.floatBinaryTag(floatValue);

            Assertions.assertEquals(tag, deserialize(serialize(tag)));
        }
    }
}
