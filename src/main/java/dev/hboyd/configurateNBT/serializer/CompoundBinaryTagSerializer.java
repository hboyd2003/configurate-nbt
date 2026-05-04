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

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Serializer for {@link CompoundBinaryTag}s.
 */
public final class CompoundBinaryTagSerializer implements TypeSerializer<CompoundBinaryTag> {
    public static final CompoundBinaryTagSerializer INSTANCE = new CompoundBinaryTagSerializer();

    private CompoundBinaryTagSerializer() {}

    @Override
    public CompoundBinaryTag deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        final CompoundBinaryTag.Builder compoundBinaryTag = CompoundBinaryTag.builder();
        if (!node.isMap())
            throw new SerializationException("ConfigurationNode of CompoundBinaryTag must be a Map");

        final TypeSerializer<BinaryTag> binaryTagSerializer = requireNonNull(node.options().serializers().get(BinaryTag.class), "BinaryTag serializer");

        for (final Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
            compoundBinaryTag.put((String) entry.getKey(),
                    binaryTagSerializer.deserialize(type, entry.getValue()));
        }

        return compoundBinaryTag.build();
    }

    @Override
    public void serialize(final Type type, @Nullable final CompoundBinaryTag compoundBinaryTag, final ConfigurationNode node) throws SerializationException {
        if (compoundBinaryTag == null) return;

        final TypeSerializer<BinaryTag> binaryTagSerializer = requireNonNull(node.options().serializers().get(BinaryTag.class), "BinaryTag serializer");

        for (final Map.Entry<String, ? extends BinaryTag> entry : compoundBinaryTag) {
            final ConfigurationNode entryNode = node.node(entry.getKey());
            binaryTagSerializer.serialize(type, entry.getValue(), entryNode);
        }
    }
}
