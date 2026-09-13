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

package dev.hboyd.configurate_nbt.serializer;

import org.junit.jupiter.api.BeforeEach;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Set;

abstract class AbstractBinaryTagSerializerTest {
    protected ConfigurationNode node;

    @BeforeEach
    void setUp() {
        this.node = BasicConfigurationNode.root(ConfigurationOptions.defaults()
                .nativeTypes(Set.of(
                        Integer.class, Double.class, Byte.class, Long.class, Short.class, Float.class, String.class)) // By default, without native types nothing is serialized.
                .serializers(TypeSerializerCollection.defaults().childBuilder()
                        .registerAll(BinaryTagSerializer.TYPE_UNSAFE_SERIALIZERS)
                        .build()));
    }
}
