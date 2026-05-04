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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.Scalars;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Serializer for {@link NumberBinaryTag}s that maintains types by appending SNBT-like suffixes.
 *
 * <p>Intended to be used with {@link org.spongepowered.configurate.loader.ConfigurationLoader} implementations that
 * do not maintain types.</p>
 */
public final class TypeSafeNumberBinaryTagSerializer implements TypeSerializer<NumberBinaryTag> {
    public static final TypeSafeNumberBinaryTagSerializer INSTANCE = new TypeSafeNumberBinaryTagSerializer();
    private static final char INT_SUFFIX = 'i';
    private static final char DOUBLE_SUFFIX = 'd';
    private static final char BYTE_SUFFIX = 'b';
    private static final char LONG_SUFFIX = 'l';
    private static final char SHORT_SUFFIX = 's';
    private static final char FLOAT_SUFFIX = 'f';

    private TypeSafeNumberBinaryTagSerializer() {}

    @Override
    public @Nullable NumberBinaryTag deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        String string = node.get(String.class);
        if (string == null) return null;

        char suffix = Character.toLowerCase(string.charAt(string.length() - 1));
        if (Character.isDigit(suffix)) { // Int and double can lack a suffix
            if (string.contains(".")) suffix = 'd';
            else suffix = 'i';
        } else {
            string = string.substring(0, string.length() - 1);
        }

        try {
            return switch (suffix) {
                case INT_SUFFIX -> IntBinaryTag.intBinaryTag(Integer.parseInt(string));
                case DOUBLE_SUFFIX -> DoubleBinaryTag.doubleBinaryTag(Double.parseDouble(string));
                case BYTE_SUFFIX -> ByteBinaryTag.byteBinaryTag(Byte.parseByte(string));
                case LONG_SUFFIX -> LongBinaryTag.longBinaryTag(Long.parseLong(string));
                case SHORT_SUFFIX -> ShortBinaryTag.shortBinaryTag(Short.parseShort(string));
                case FLOAT_SUFFIX -> FloatBinaryTag.floatBinaryTag(Float.parseFloat(string));
                default -> throw new SerializationException("Unknown number binary tag: " + string);
            };
        } catch (final NumberFormatException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(final Type type, @Nullable final NumberBinaryTag numberBinaryTag, final ConfigurationNode node) throws SerializationException {
        if (numberBinaryTag == null) return;

        switch (numberBinaryTag) {
            case final IntBinaryTag tag -> node.set(Scalars.STRING.type(), Integer.toString(tag.value()));
            case final DoubleBinaryTag tag -> node.set(Scalars.STRING.type(), Double.toString(tag.value()));
            case final ByteBinaryTag tag -> node.set(Scalars.STRING.type(), Byte.toString(tag.value()) + BYTE_SUFFIX);
            case final LongBinaryTag tag -> node.set(Scalars.STRING.type(), Long.toString(tag.value()) + LONG_SUFFIX);
            case final ShortBinaryTag tag -> node.set(Scalars.STRING.type(), Short.toString(tag.value()) + SHORT_SUFFIX);
            case final FloatBinaryTag tag -> node.set(Scalars.STRING.type(), Float.toString(tag.value()) + FLOAT_SUFFIX);
            default -> throw new SerializationException("Unknown number binary tag: " + numberBinaryTag);
        }
    }
}
