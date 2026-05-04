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

import net.kyori.adventure.nbt.StringBinaryTag;
import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * Serializer for {@link StringBinaryTag}s.
 */
public final class StringBinaryTagSerializer extends ScalarSerializer<StringBinaryTag> {
    public static final StringBinaryTagSerializer INSTANCE = new StringBinaryTagSerializer();

    private StringBinaryTagSerializer() {
        super(StringBinaryTag.class);
    }

    @Override
    public StringBinaryTag deserialize(final Type type, final Object obj) {
        return StringBinaryTag.stringBinaryTag(obj.toString());
    }

    @Override
    protected Object serialize(final StringBinaryTag stringBinaryTag, final Predicate<Class<?>> typeSupported) {
        return stringBinaryTag.value();
    }
}
