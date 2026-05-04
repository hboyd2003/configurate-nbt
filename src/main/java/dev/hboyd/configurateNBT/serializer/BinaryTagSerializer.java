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
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.NumberBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * Serializer for {@link BinaryTag}s.
 *
 * <p>Optionally supports {@link org.spongepowered.configurate.loader.ConfigurationLoader} implementations that do not maintain
 * numeric type saftey.</p>
 *
 * @see <a href="https://minecraft.wiki/w/NBT_format#Data_types">Minecraft Wiki - SNBT Data types</a>
 * @see BinaryTagSerializer#TYPE_UNSAFE
 * @see BinaryTagSerializer#TYPE_SAFE
 * @see BinaryTagSerializer#TYPE_UNSAFE_SERIALIZERS
 * @see BinaryTagSerializer#TYPE_SAFE_SERIALIZERS
 */
public final class BinaryTagSerializer implements TypeSerializer<BinaryTag> {
    /**
     * Serializer for {@link BinaryTag}s.
     *
     * <p>Types will not be maintained if the used {@link org.spongepowered.configurate.loader.ConfigurationLoader}
     * implementation does not fully maintain types.</p>
     *
     * <p>Requires serializers for each {@link BinaryTag} type to be registered with the loader.</p>.
     */
    public static final BinaryTagSerializer TYPE_UNSAFE = new BinaryTagSerializer(false);

    /**
     * Serializer for {@link BinaryTag}s which attempts to coerce strings into {@link NumberBinaryTag}
     * before creating {@link StringBinaryTag}s with them.
     *
     * <p>Requires serializers for each {@link BinaryTag} type to be registered with the loader.</p>.
     */
    public static final BinaryTagSerializer TYPE_SAFE = new BinaryTagSerializer(true);

    /**
     * Collection of all "type-unsafe" BinaryTag serializers.
     *
     * <p>Types will not be maintained if the used {@link org.spongepowered.configurate.loader.ConfigurationLoader}
     * implementation does not fully maintain types.</p>
     *
     * @see BinaryTagSerializer#TYPE_UNSAFE
     */
    public static final TypeSerializerCollection TYPE_UNSAFE_SERIALIZERS = TypeSerializerCollection.builder()
            .registerExact(BinaryTag.class, BinaryTagSerializer.TYPE_UNSAFE)
            .register(CompoundBinaryTag.class, CompoundBinaryTagSerializer.INSTANCE)
            .register(NumberBinaryTag.class, NumberBinaryTagSerializer.INSTANCE)
            .register(StringBinaryTag.class, StringBinaryTagSerializer.INSTANCE)
            .register(ListBinaryTag.class, ListBinaryTagSerializer.INSTANCE)
            .register(ArrayBinaryTag.class, ArrayBinaryTagSerializer.INSTANCE)
            .build();

    /**
     * Collection of all "type-safe" BinaryTag serializers.
     *
     * <p>Types are maintained even if the used {@link org.spongepowered.configurate.loader.ConfigurationLoader}
     * implementation does not maintain them by appending SNBT-like suffixes to numerical values.</p>
     *
     * @see BinaryTagSerializer#TYPE_SAFE
     * @see TypeSafeNumberBinaryTagSerializer
     */
    public static final TypeSerializerCollection TYPE_SAFE_SERIALIZERS = TypeSerializerCollection.builder()
            .registerExact(BinaryTag.class, BinaryTagSerializer.TYPE_SAFE)
            .register(CompoundBinaryTag.class, CompoundBinaryTagSerializer.INSTANCE)
            .register(NumberBinaryTag.class, TypeSafeNumberBinaryTagSerializer.INSTANCE)
            .register(StringBinaryTag.class, StringBinaryTagSerializer.INSTANCE)
            .register(ListBinaryTag.class, ListBinaryTagSerializer.INSTANCE)
            .register(ArrayBinaryTag.class, ArrayBinaryTagSerializer.INSTANCE)
            .build();

    private final boolean typesafe;

    private BinaryTagSerializer(final boolean typesafe) {
        this.typesafe = typesafe;
    }

    @Override
    @NullMarked
    public BinaryTag deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        if (node.isList()) return this.deserializeList(node);
        else if (node.isMap()) return node.options().serializers().get(CompoundBinaryTag.class)
                .deserialize(CompoundBinaryTag.class, node);

        return this.deserializePrimitive(node);
    }

    private BinaryTag deserializePrimitive(final ConfigurationNode node) throws SerializationException {
        if (this.typesafe && node.raw() instanceof String) {
            try {
                return node.options().serializers().get(NumberBinaryTag.class).deserialize(NumberBinaryTag.class, node);
            } catch (final SerializationException _) {
                return StringBinaryTag.stringBinaryTag(node.getString());
            }
        }

        // Deserialize as "type-unsafe" which relies on the loader to load each as the correct type.
        return switch (node.raw()) {
            case final String ignored -> node.options().serializers().get(StringBinaryTag.class).deserialize(StringBinaryTag.class, node);
            case final Array ignored -> node.options().serializers().get(ArrayBinaryTag.class).deserialize(ArrayBinaryTag.class, node);
            case final Number ignored -> node.options().serializers().get(NumberBinaryTag.class).deserialize(NumberBinaryTag.class, node);
            default -> throw new IllegalStateException("Unexpected type: " + node.raw());
        };
    }

    private BinaryTag deserializeList(final ConfigurationNode node) throws SerializationException {
        final ListBinaryTag listBinaryTag = node.options().serializers().get(ListBinaryTag.class).deserialize(ListBinaryTag.class, node);
        if (listBinaryTag.isEmpty()) return listBinaryTag;

        // Check if this list can be an ArrayBinaryTag
        final BinaryTagType<?> listElementType = listBinaryTag.get(0).type();
        if (listElementType != BinaryTagTypes.INT
                && listElementType != BinaryTagTypes.BYTE
                && listElementType != BinaryTagTypes.LONG)
            return listBinaryTag; // Return if list cannot be an array

        // Check for heterogeneity
        if (listBinaryTag.stream().anyMatch(binaryTag -> binaryTag.type() != listElementType))
            return listBinaryTag;

        // Convert to array
        return node.options().serializers().get(ArrayBinaryTag.class).deserialize(ArrayBinaryTag.class, node);
    }

    @Override
    public void serialize(final Type type, @Nullable final BinaryTag binaryTag, final ConfigurationNode node) throws SerializationException {
        if (binaryTag == null) return;
        switch (binaryTag) {
            case final NumberBinaryTag numberBinaryTag ->
                    node.options().serializers().get(NumberBinaryTag.class).serialize(NumberBinaryTag.class, numberBinaryTag, node);
            case final StringBinaryTag stringBinaryTag ->
                    node.options().serializers().get(StringBinaryTag.class).serialize(StringBinaryTag.class, stringBinaryTag, node);
            case final ListBinaryTag listBinaryTag ->
                    node.options().serializers().get(ListBinaryTag.class).serialize(ListBinaryTag.class, listBinaryTag, node);
            case final CompoundBinaryTag compoundBinaryTag ->
                    node.options().serializers().get(CompoundBinaryTag.class).serialize(CompoundBinaryTag.class, compoundBinaryTag, node);
            case final ArrayBinaryTag arrayBinaryTag ->
                    node.options().serializers().get(ArrayBinaryTag.class).serialize(ArrayBinaryTag.class, arrayBinaryTag, node);
            default -> throw new IllegalStateException("Unknown tag type: " + binaryTag.type());
        }
    }
}
