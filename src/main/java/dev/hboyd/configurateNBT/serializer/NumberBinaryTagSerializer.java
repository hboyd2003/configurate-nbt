package dev.hboyd.configurateNBT.serializer;

import net.kyori.adventure.nbt.*;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class NumberBinaryTagSerializer extends ScalarSerializer<NumberBinaryTag> {
    public static final NumberBinaryTagSerializer INSTANCE = new NumberBinaryTagSerializer();

    private NumberBinaryTagSerializer() {
        super(NumberBinaryTag.class);
    }

    @Override
    @NullMarked
    public NumberBinaryTag deserialize(Type type, Object obj) throws SerializationException {
        return switch (obj) {
            case Integer value -> IntBinaryTag.intBinaryTag(value);
            case Double value -> DoubleBinaryTag.doubleBinaryTag(value);
            case Byte value -> ByteBinaryTag.byteBinaryTag(value);
            case Long value -> LongBinaryTag.longBinaryTag(value);
            case Float value -> FloatBinaryTag.floatBinaryTag(value);
            case Short value -> ShortBinaryTag.shortBinaryTag(value);

            default -> throw new CoercionFailedException(type, obj, "NumberBinaryTag");
        };
    }

    @Override
    @NullMarked
    protected Object serialize(NumberBinaryTag tag, Predicate<Class<?>> typeSupported) {
        return switch (tag) {
            case IntBinaryTag intTag -> intTag.value();
            case DoubleBinaryTag doubleTag -> doubleTag.value();
            case ByteBinaryTag byteTag -> byteTag.value();
            case LongBinaryTag longTag -> longTag.value();
            case FloatBinaryTag floatTag -> floatTag.value();
            case ShortBinaryTag shortTag -> shortTag.value();
            default -> throw new IllegalStateException("Unknown type: " + tag.getClass());
        };
    }
}
