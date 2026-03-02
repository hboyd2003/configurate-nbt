package dev.hboyd.configurateNBT.serializer;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ListBinaryTagSerializer implements TypeSerializer<ListBinaryTag> {
    public static final ListBinaryTagSerializer INSTANCE = new ListBinaryTagSerializer();

    private ListBinaryTagSerializer() {}

    @Override
    public ListBinaryTag deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final TypeSerializer<BinaryTag> binaryTagSerializer =
                requireNonNull(node.options().serializers().get(BinaryTag.class), "BinaryTag serializer");

        for (ConfigurationNode childNode : node.childrenList())
            binaryTagSerializer.deserialize(type, childNode);

        return ListBinaryTag.builder().build();
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
