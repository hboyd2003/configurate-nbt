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
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.ListBinaryTag;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Serializer for {@link ListBinaryTag}s.
 */
public final class ListBinaryTagSerializer implements TypeSerializer<ListBinaryTag> {
    public static final ListBinaryTagSerializer INSTANCE = new ListBinaryTagSerializer();

    private ListBinaryTagSerializer() {}

    @Override
    public ListBinaryTag deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        final TypeSerializer<BinaryTag> binaryTagSerializer =
                requireNonNull(node.options().serializers().get(BinaryTag.class), "BinaryTag serializer");

        final List<BinaryTag> binaryTags = new ArrayList<>(node.childrenList().size());
        BinaryTagType<?> listType = null;
        boolean heterogeneous = false;
        for (final ConfigurationNode childNode : node.childrenList()) {
            final BinaryTag binaryTag = binaryTagSerializer.deserialize(type, childNode);

            if (listType == null) listType = binaryTag.type();
            else if (listType != binaryTag.type()) heterogeneous = true;

            binaryTags.add(binaryTag);
        }

        final ListBinaryTag.Builder<?> builder;
        if (heterogeneous) builder = ListBinaryTag.heterogeneousListBinaryTag(binaryTags.size());
        else builder = ListBinaryTag.builder(listType, binaryTags.size());

        builder.add((Iterable) binaryTags);

        return builder.build();
    }

    @Override
    public void serialize(final Type type, @Nullable final ListBinaryTag listBinaryTag, final ConfigurationNode node) throws SerializationException {
        if (listBinaryTag == null) return;

        node.setList(BinaryTag.class, listBinaryTag.stream().toList());
    }

    @Override
    public @Nullable ListBinaryTag emptyValue(final Type specificType, final ConfigurationOptions options) {
        return ListBinaryTag.builder().build();
    }
}
