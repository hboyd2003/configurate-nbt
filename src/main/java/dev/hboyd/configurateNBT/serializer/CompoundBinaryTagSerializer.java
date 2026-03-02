package dev.hboyd.configurateNBT.serializer;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@NullMarked
public class CompoundBinaryTagSerializer implements TypeSerializer<CompoundBinaryTag> {
    public static final CompoundBinaryTagSerializer INSTANCE = new CompoundBinaryTagSerializer();

    private CompoundBinaryTagSerializer() {}

    @Override
    public CompoundBinaryTag deserialize(Type type, ConfigurationNode node) throws SerializationException {
        CompoundBinaryTag.Builder compoundBinaryTag = CompoundBinaryTag.builder();
        if (!node.isMap())
            throw new SerializationException("ConfigurationNode of CompoundBinaryTag must be a Map");

        final TypeSerializer<BinaryTag> binaryTagSerializer = requireNonNull(node.options().serializers().get(BinaryTag.class), "BinaryTag serializer");

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
            compoundBinaryTag.put((String) entry.getKey(),
                    binaryTagSerializer.deserialize(type, entry.getValue()));
        }

        return compoundBinaryTag.build();
    }

    @Override
    public void serialize(Type type, @Nullable CompoundBinaryTag compoundBinaryTag, ConfigurationNode node) throws SerializationException {
        if (compoundBinaryTag == null) return;

        final TypeSerializer<BinaryTag> binaryTagSerializer =requireNonNull(node.options().serializers().get(BinaryTag.class), "BinaryTag serializer");

        for (Map.Entry<String, ? extends BinaryTag> entry : compoundBinaryTag) {
            ConfigurationNode entryNode = node.node(entry.getKey());
            binaryTagSerializer.serialize(type, entry.getValue(), entryNode);
        }
    }
}
