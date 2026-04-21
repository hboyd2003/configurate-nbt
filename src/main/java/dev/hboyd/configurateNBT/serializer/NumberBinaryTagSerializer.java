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

import net.kyori.adventure.nbt.*;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class NumberBinaryTagSerializer extends ScalarSerializer<NumberBinaryTag> {
    public static final NumberBinaryTagSerializer INSTANCE = new NumberBinaryTagSerializer();

    private NumberBinaryTagSerializer() {
        super(NumberBinaryTag.class);
    }

    @Override
    @NullMarked
    public NumberBinaryTag deserialize(Type type, Object obj) throws SerializationException {
        return switch (obj) {
            case Integer value -> IntBinaryTag.intBinaryTag(value);
            case Double value -> DoubleBinaryTag.doubleBinaryTag(value);
            case Byte value -> ByteBinaryTag.byteBinaryTag(value);
            case Long value -> LongBinaryTag.longBinaryTag(value);
            case Float value -> FloatBinaryTag.floatBinaryTag(value);
            case Short value -> ShortBinaryTag.shortBinaryTag(value);

            default -> throw new CoercionFailedException(type, obj, "NumberBinaryTag");
        };
    }

    @Override
    @NullMarked
    protected Object serialize(NumberBinaryTag tag, Predicate<Class<?>> typeSupported) {
        return switch (tag) {
            case IntBinaryTag intTag -> intTag.value();
            case DoubleBinaryTag doubleTag -> doubleTag.value();
            case ByteBinaryTag byteTag -> byteTag.value();
            case LongBinaryTag longTag -> longTag.value();
            case FloatBinaryTag floatTag -> floatTag.value();
            case ShortBinaryTag shortTag -> shortTag.value();
            default -> throw new IllegalStateException("Unknown type: " + tag.getClass());
        };
    }
}
