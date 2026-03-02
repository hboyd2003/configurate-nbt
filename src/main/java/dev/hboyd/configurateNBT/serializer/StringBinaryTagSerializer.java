package dev.hboyd.configurateNBT.serializer;

import net.kyori.adventure.nbt.StringBinaryTag;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@NullMarked
public class StringBinaryTagSerializer extends ScalarSerializer<StringBinaryTag> {
    public static final StringBinaryTagSerializer INSTANCE = new StringBinaryTagSerializer();

    private StringBinaryTagSerializer() {
        super(StringBinaryTag.class);
    }

    @Override
    public StringBinaryTag deserialize(Type type, Object obj) {
        return StringBinaryTag.stringBinaryTag(obj.toString());
    }

    @Override
    protected Object serialize(StringBinaryTag stringBinaryTag, Predicate<Class<?>> typeSupported) {
        return stringBinaryTag.value();
    }
}
