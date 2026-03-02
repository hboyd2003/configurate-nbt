package dev.hboyd.configurateNBT.serializer;

import net.kyori.adventure.nbt.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class ArrayBinaryTagSerializer implements TypeSerializer<ArrayBinaryTag> {
    public static final ArrayBinaryTagSerializer INSTANCE = new ArrayBinaryTagSerializer();

    private ArrayBinaryTagSerializer() {}

    @Override
    public ArrayBinaryTag deserialize(Type type, ConfigurationNode node) throws SerializationException {
        ListBinaryTag listBinaryTag = node.options().serializers().get(ListBinaryTag.class).deserialize(ListBinaryTag.class, node);
        listBinaryTag.unwrapHeterogeneity();
        if (listBinaryTag.elementType() == BinaryTagTypes.INT) {
            int[] intArray = new int[listBinaryTag.size()];
            for (int i = 0; i < intArray.length; i++) {
                intArray[i] = listBinaryTag.getInt(i);
            }
            return IntArrayBinaryTag.intArrayBinaryTag(intArray);
        } else if (listBinaryTag.elementType() == BinaryTagTypes.BYTE) {
            byte[] byteArray = new byte[listBinaryTag.size()];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = listBinaryTag.getByte(i);
            }
            return ByteArrayBinaryTag.byteArrayBinaryTag(byteArray);
        } else if (listBinaryTag.elementType() == BinaryTagTypes.LONG) {
            long[] longArray = new long[listBinaryTag.size()];
            for (int i = 0; i < longArray.length; i++) {
                longArray[i] = listBinaryTag.getLong(i);
            }
            return LongArrayBinaryTag.longArrayBinaryTag(longArray);
        } else throw new SerializationException("Unknown array binary tag will element type of " + listBinaryTag.elementType());
    }

    @Override
    public void serialize(Type type, @Nullable ArrayBinaryTag arrayBinaryTag, ConfigurationNode node) throws SerializationException {
        if (arrayBinaryTag == null) return;
        switch (arrayBinaryTag) {
            case IntArrayBinaryTag intArrayBinaryTag -> node.set(int[].class, intArrayBinaryTag.value());
            case ByteArrayBinaryTag intArrayBinaryTag -> node.set(byte[].class, intArrayBinaryTag.value());
            case LongArrayBinaryTag longArrayBinaryTag -> node.set(long[].class, longArrayBinaryTag.value());
            default -> throw new IllegalStateException("Unknown array binary tag type: " + arrayBinaryTag);
        }
    }
}
