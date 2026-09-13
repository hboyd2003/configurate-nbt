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
import org.spongepowered.configurate.serialize.SerializationException;

class NumberBinaryTagSerializerTest extends AbstractBinaryTagSerializerTest {

    @Nested
    class Serialization {

        @ParameterizedTest
        @ValueSource(ints = {0, -99, 114, Integer.MAX_VALUE, Integer.MIN_VALUE})
        void intBinaryTagSerializesToInteger(final int integer) {
            final Integer serializedInteger = (Integer) NumberBinaryTagSerializer.INSTANCE
                    .serialize(IntBinaryTag.intBinaryTag(integer), _ -> true);

            Assertions.assertEquals(integer, serializedInteger);
        }

        @ParameterizedTest
        @ValueSource(doubles = {0, -99.4968, 114.8542, Double.MAX_VALUE, Double.MIN_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY})
        void doubleBinaryTagSerializesToDouble(final double doubleValue) {
            final Double serializedDouble = (Double) NumberBinaryTagSerializer.INSTANCE
                    .serialize(DoubleBinaryTag.doubleBinaryTag(doubleValue), _ -> true);

            Assertions.assertEquals(doubleValue, serializedDouble);
        }

        @ParameterizedTest
        @ValueSource(bytes = {0, -99, 114, Byte.MAX_VALUE, Byte.MIN_VALUE})
        void byteBinaryTagSerializesToByte(final byte byteValue) {
            final Byte serializedByte = (Byte) NumberBinaryTagSerializer.INSTANCE
                    .serialize(ByteBinaryTag.byteBinaryTag(byteValue), _ -> true);

            Assertions.assertEquals(byteValue, serializedByte);
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -991876462, 1145461646, Long.MAX_VALUE, Long.MIN_VALUE})
        void longBinaryTagSerializesToLong(final long longValue) {
            final Long serializedLong = (Long) NumberBinaryTagSerializer.INSTANCE
                    .serialize(LongBinaryTag.longBinaryTag(longValue), _ -> true);

            Assertions.assertEquals(longValue, serializedLong);
        }

        @ParameterizedTest
        @ValueSource(floats = {0, -99.9419841f, 114.54894f, Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY})
        void floatBinaryTagSerializesToFloat(final float floatValue) {
            final Float serializedFloat = (Float) NumberBinaryTagSerializer.INSTANCE
                    .serialize(FloatBinaryTag.floatBinaryTag(floatValue), _ -> true);

            Assertions.assertEquals(floatValue, serializedFloat);
        }

        @ParameterizedTest
        @ValueSource(shorts = {0, -9945, 11454, Short.MAX_VALUE, Short.MIN_VALUE})
        void shortBinaryTagSerializesToShort(final short shortValue) {
            final Short serializedShort = (Short) NumberBinaryTagSerializer.INSTANCE
                    .serialize(ShortBinaryTag.shortBinaryTag(shortValue), _ -> true);

            Assertions.assertEquals(shortValue, serializedShort);
        }
    }

    @Nested
    class Deserialization {

        @ParameterizedTest
        @ValueSource(ints = {0, -9945, 11454, Integer.MAX_VALUE, Integer.MIN_VALUE})
        void integerDeserializesToIntegerBinaryTag(final int integerValue) throws SerializationException {
            final IntBinaryTag deserializedInteger = (IntBinaryTag) NumberBinaryTagSerializer.INSTANCE
                    .deserialize(integerValue);

            Assertions.assertEquals(IntBinaryTag.intBinaryTag(integerValue), deserializedInteger);
        }

        @ParameterizedTest
        @ValueSource(doubles = {0, -99.4968, 114.8542, Double.MAX_VALUE, Double.MIN_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY})
        void doubleDeserializesToDoubleBinaryTag(final double doubleValue) throws SerializationException {
            final DoubleBinaryTag deserializedDouble = (DoubleBinaryTag) NumberBinaryTagSerializer.INSTANCE
                    .deserialize(doubleValue);

            Assertions.assertEquals(DoubleBinaryTag.doubleBinaryTag(doubleValue), deserializedDouble);
        }

        @ParameterizedTest
        @ValueSource(bytes = {0, -99, 114, Byte.MAX_VALUE, Byte.MIN_VALUE})
        void byteDeserializesToByteBinaryTag(final byte byteValue) throws SerializationException {
            final ByteBinaryTag deserializedByte = (ByteBinaryTag) NumberBinaryTagSerializer.INSTANCE
                    .deserialize(byteValue);

            Assertions.assertEquals(ByteBinaryTag.byteBinaryTag(byteValue), deserializedByte);
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -991876462, 1145461646, Long.MAX_VALUE, Long.MIN_VALUE})
        void longDeserializesToLongBinaryTag(final long longValue) throws SerializationException {
            final LongBinaryTag deserializedLong = (LongBinaryTag) NumberBinaryTagSerializer.INSTANCE
                    .deserialize(longValue);

            Assertions.assertEquals(LongBinaryTag.longBinaryTag(longValue), deserializedLong);
        }

        @ParameterizedTest
        @ValueSource(floats = {0, -99.9419841f, 114.54894f, Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY})
        void floatDeserializesToFloatBinaryTag(final float floatValue) throws SerializationException {
            final FloatBinaryTag deserializedFloat = (FloatBinaryTag) NumberBinaryTagSerializer.INSTANCE
                    .deserialize(floatValue);

            Assertions.assertEquals(FloatBinaryTag.floatBinaryTag(floatValue), deserializedFloat);
        }

        @ParameterizedTest
        @ValueSource(shorts = {0, -9945, 11454, Short.MAX_VALUE, Short.MIN_VALUE})
        void shortDeserializesToShortBinaryTag(final short shortValue) throws SerializationException {
            final ShortBinaryTag deserializedShort = (ShortBinaryTag) NumberBinaryTagSerializer.INSTANCE
                    .deserialize(shortValue);

            Assertions.assertEquals(ShortBinaryTag.shortBinaryTag(shortValue), deserializedShort);
        }
    }

    @Nested
    class RoundTrip {

        @Test
        void intBinaryTagDoesNotChangeAfterRoundTrip() throws SerializationException {
            final IntBinaryTag tag = IntBinaryTag.intBinaryTag(114);

            NumberBinaryTagSerializer.INSTANCE.serialize(NumberBinaryTag.class, tag, NumberBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag,
                    NumberBinaryTagSerializer.INSTANCE.deserialize(NumberBinaryTag.class, NumberBinaryTagSerializerTest.this.node));
        }

        @Test
        void doubleBinaryTagDoesNotChangeAfterRoundTrip() throws SerializationException {
            final DoubleBinaryTag tag = DoubleBinaryTag.doubleBinaryTag(114.8542);

            NumberBinaryTagSerializer.INSTANCE.serialize(NumberBinaryTag.class, tag, NumberBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag,
                    NumberBinaryTagSerializer.INSTANCE.deserialize(NumberBinaryTag.class, NumberBinaryTagSerializerTest.this.node));
        }

        @Test
        void byteBinaryTagDoesNotChangeAfterRoundTrip() throws SerializationException {
            final ByteBinaryTag tag = ByteBinaryTag.byteBinaryTag((byte) 114);

            NumberBinaryTagSerializer.INSTANCE.serialize(NumberBinaryTag.class, tag, NumberBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag,
                    NumberBinaryTagSerializer.INSTANCE.deserialize(NumberBinaryTag.class, NumberBinaryTagSerializerTest.this.node));
        }

        @Test
        void longBinaryTagDoesNotChangeAfterRoundTrip() throws SerializationException {
            final LongBinaryTag tag = LongBinaryTag.longBinaryTag(1145461646L);

            NumberBinaryTagSerializer.INSTANCE.serialize(NumberBinaryTag.class, tag, NumberBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag,
                    NumberBinaryTagSerializer.INSTANCE.deserialize(NumberBinaryTag.class, NumberBinaryTagSerializerTest.this.node));
        }

        @Test
        void floatBinaryTagDoesNotChangeAfterRoundTrip() throws SerializationException {
            final FloatBinaryTag tag = FloatBinaryTag.floatBinaryTag(114.54894f);

            NumberBinaryTagSerializer.INSTANCE.serialize(NumberBinaryTag.class, tag, NumberBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag,
                    NumberBinaryTagSerializer.INSTANCE.deserialize(NumberBinaryTag.class, NumberBinaryTagSerializerTest.this.node));
        }

        @Test
        void shortBinaryTagDoesNotChangeAfterRoundTrip() throws SerializationException {
            final ShortBinaryTag tag = ShortBinaryTag.shortBinaryTag((short) 11454);

            NumberBinaryTagSerializer.INSTANCE.serialize(NumberBinaryTag.class, tag, NumberBinaryTagSerializerTest.this.node);

            Assertions.assertEquals(tag,
                    NumberBinaryTagSerializer.INSTANCE.deserialize(NumberBinaryTag.class, NumberBinaryTagSerializerTest.this.node));
        }
    }
}
