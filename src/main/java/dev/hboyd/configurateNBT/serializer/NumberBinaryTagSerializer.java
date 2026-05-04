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

import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.NumberBinaryTag;
import net.kyori.adventure.nbt.ShortBinaryTag;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * Serializer for {@link NumberBinaryTag}s.
 */
public final class NumberBinaryTagSerializer extends ScalarSerializer<NumberBinaryTag> {
    public static final NumberBinaryTagSerializer INSTANCE = new NumberBinaryTagSerializer();

    private NumberBinaryTagSerializer() {
        super(NumberBinaryTag.class);
    }

    @Override
    @NullMarked
    public NumberBinaryTag deserialize(final Type type, final Object obj) throws SerializationException {
        return switch (obj) {
            case final Integer value -> IntBinaryTag.intBinaryTag(value);
            case final Double value -> DoubleBinaryTag.doubleBinaryTag(value);
            case final Byte value -> ByteBinaryTag.byteBinaryTag(value);
            case final Long value -> LongBinaryTag.longBinaryTag(value);
            case final Float value -> FloatBinaryTag.floatBinaryTag(value);
            case final Short value -> ShortBinaryTag.shortBinaryTag(value);
            default -> throw new CoercionFailedException(type, obj, "NumberBinaryTag");
        };
    }

    @Override
    @NullMarked
    protected Object serialize(final NumberBinaryTag tag, final Predicate<Class<?>> typeSupported) {
        return switch (tag) {
            case final IntBinaryTag intTag -> intTag.value();
            case final DoubleBinaryTag doubleTag -> doubleTag.value();
            case final ByteBinaryTag byteTag -> byteTag.value();
            case final LongBinaryTag longTag -> longTag.value();
            case final FloatBinaryTag floatTag -> floatTag.value();
            case final ShortBinaryTag shortTag -> shortTag.value();
            default -> throw new IllegalStateException("Unknown type: " + tag.getClass());
        };
    }
}
