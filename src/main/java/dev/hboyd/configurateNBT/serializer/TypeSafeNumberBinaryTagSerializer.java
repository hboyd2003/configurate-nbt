package dev.hboyd.configurateNBT.serializer;

import net.kyori.adventure.nbt.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.Scalars;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class TypeSafeNumberBinaryTagSerializer implements TypeSerializer<NumberBinaryTag> {
    public static final TypeSafeNumberBinaryTagSerializer INSTANCE = new TypeSafeNumberBinaryTagSerializer();
    private static final char INT_SUFFIX = 'i';
    private static final char DOUBLE_SUFFIX = 'd';
    private static final char BYTE_SUFFIX = 'b';
    private static final char LONG_SUFFIX = 'l';
    private static final char SHORT_SUFFIX = 's';
    private static final char FLOAT_SUFFIX = 'f';

    private TypeSafeNumberBinaryTagSerializer() {}

    @Override
    public @Nullable NumberBinaryTag deserialize(Type type, ConfigurationNode node) throws SerializationException {
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
        } catch (NumberFormatException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(Type type, @Nullable NumberBinaryTag numberBinaryTag, ConfigurationNode node) throws SerializationException {
        if (numberBinaryTag == null) return;

        switch (numberBinaryTag) {
            case IntBinaryTag tag -> node.set(Scalars.STRING.type(), Integer.toString(tag.value()));
            case DoubleBinaryTag tag -> node.set(Scalars.STRING.type(), Double.toString(tag.value()));
            case ByteBinaryTag tag -> node.set(Scalars.STRING.type(), Byte.toString(tag.value()) + BYTE_SUFFIX);
            case LongBinaryTag tag -> node.set(Scalars.STRING.type(), Long.toString(tag.value()) + LONG_SUFFIX);
            case ShortBinaryTag tag -> node.set(Scalars.STRING.type(), Short.toString(tag.value()) + SHORT_SUFFIX);
            case FloatBinaryTag tag -> node.set(Scalars.STRING.type(), Float.toString(tag.value()) + FLOAT_SUFFIX);
            default -> throw new SerializationException("Unknown number binary tag: " + numberBinaryTag);
        }
    }
}