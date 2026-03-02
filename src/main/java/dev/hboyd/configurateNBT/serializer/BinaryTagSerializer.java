package dev.hboyd.configurateNBT.serializer;

import net.kyori.adventure.nbt.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * Provides serializers for {@link BinaryTag}.
 * <p>
 * "type-unsafe" relies on the {@link org.spongepowered.configurate.loader.ConfigurationLoader} to produce correctly typed numeric values in deserialization.
 * <p>
 * "type-safe" ensures that for all {@link org.spongepowered.configurate.loader.ConfigurationLoader} deserialization will produce accurate numeric types.
 * This is done by storing numeric values with SNBT like primitive suffixes.
 * @see <a href="https://minecraft.wiki/w/NBT_format#Data_types">Minecraft Wiki - SNBT Data types</a>
 */
@NullMarked
public class BinaryTagSerializer implements TypeSerializer<BinaryTag> {
    public static final BinaryTagSerializer TYPE_UNSAFE = new BinaryTagSerializer(false);
    public static final BinaryTagSerializer TYPE_SAFE = new BinaryTagSerializer(true);

    /**
     * Collection of all "type-unsafe" BinaryTag serializers
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
     * Collection of all "type-safe" BinaryTag serializers
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

    private BinaryTagSerializer(boolean typesafe) {
        this.typesafe = typesafe;
    }

    @Override
    @NullMarked
    public BinaryTag deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.isList()) return deserializeList(node);
        else if (node.isMap()) return node.options().serializers().get(CompoundBinaryTag.class)
                .deserialize(CompoundBinaryTag.class, node);

        return deserializePrimitive(node);
    }

    private BinaryTag deserializePrimitive(@NonNull ConfigurationNode node) throws SerializationException {
        if (typesafe) {
            try {
                return node.options().serializers().get(NumberBinaryTag.class).deserialize(NumberBinaryTag.class, node);
            } catch (SerializationException e) {
                return StringBinaryTag.stringBinaryTag(node.getString());
            }
        }

        // Deserialize as "type-unsafe" which relies on the loader to load each as the correct type.
        return switch (node.raw()) {
            case String ignored -> node.options().serializers().get(StringBinaryTag.class).deserialize(StringBinaryTag.class, node);
            case Array ignored -> node.options().serializers().get(ArrayBinaryTag.class).deserialize(ArrayBinaryTag.class, node);
            case Number ignored -> node.options().serializers().get(NumberBinaryTag.class).deserialize(NumberBinaryTag.class, node);
            default -> throw new IllegalStateException("Unexpected type: " + node.raw());
        };
    }

    private BinaryTag deserializeList(@NonNull ConfigurationNode node) throws SerializationException {
        ListBinaryTag listBinaryTag = node.options().serializers().get(ListBinaryTag.class).deserialize(ListBinaryTag.class, node);
        if (listBinaryTag.isEmpty()) return listBinaryTag;

        // Check if this list can be a ArrayBinaryTag
        BinaryTagType<?> listElementType = listBinaryTag.get(0).type();
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
    @NullMarked
    public void serialize(Type type, @Nullable BinaryTag binaryTag, ConfigurationNode node) throws SerializationException {
        if (binaryTag == null) return;
        switch (binaryTag) {
            case NumberBinaryTag numberBinaryTag ->
                    node.options().serializers().get(NumberBinaryTag.class).serialize(NumberBinaryTag.class, numberBinaryTag, node);
            case StringBinaryTag stringBinaryTag ->
                    node.options().serializers().get(StringBinaryTag.class).serialize(StringBinaryTag.class, stringBinaryTag, node);
            case ListBinaryTag listBinaryTag ->
                    node.options().serializers().get(ListBinaryTag.class).serialize(ListBinaryTag.class, listBinaryTag, node);
            case CompoundBinaryTag compoundBinaryTag ->
                    node.options().serializers().get(CompoundBinaryTag.class).serialize(CompoundBinaryTag.class, compoundBinaryTag, node);
            case ArrayBinaryTag arrayBinaryTag ->
                    node.options().serializers().get(ArrayBinaryTag.class).serialize(ArrayBinaryTag.class, arrayBinaryTag, node);
            default -> throw new IllegalStateException("Unknown tag type: " + binaryTag.type());
        }
    }
}
