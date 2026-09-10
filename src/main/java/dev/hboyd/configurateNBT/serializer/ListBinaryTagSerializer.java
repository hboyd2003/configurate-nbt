package dev.hboyd.configurateNBT.serializer;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.ListBinaryTag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ListBinaryTagSerializer implements TypeSerializer<ListBinaryTag> {
    public static final ListBinaryTagSerializer INSTANCE = new ListBinaryTagSerializer();

    private ListBinaryTagSerializer() {}

    @Override
    public ListBinaryTag deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final TypeSerializer<BinaryTag> binaryTagSerializer =
                requireNonNull(node.options().serializers().get(BinaryTag.class), "BinaryTag serializer");

        final List<BinaryTag> binaryTags = new ArrayList<>(node.childrenList().size());
        BinaryTagType<?> listType = null;
        boolean heterogeneous = false;
        for (ConfigurationNode childNode : node.childrenList()) {
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
    public void serialize(Type type, @Nullable ListBinaryTag listBinaryTag, ConfigurationNode node) throws SerializationException {
        if (listBinaryTag == null) return;

        node.setList(BinaryTag.class, listBinaryTag.stream().toList());
    }

    @Override
    public @Nullable ListBinaryTag emptyValue(Type specificType, ConfigurationOptions options) {
        return ListBinaryTag.builder().build();
    }
}
