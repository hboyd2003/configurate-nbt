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

package dev.hboyd.configurate_nbt;

import net.kyori.option.Option;
import net.kyori.option.value.ValueSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationFormat;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.BufferedInputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The configuration format for the {@link NBTConfigurationLoader}.
 */
public class NBTConfigurationFormat implements ConfigurationFormat {
    private static final Pattern PATH_SPLIT = Pattern.compile("[:/]");

    @Override
    public String id() {
        return "nbt";
    }

    @Override
    public Set<String> supportedExtensions() {
        return Set.of("nbt", "dat");
    }

    @Override
    public NBTConfigurationLoader create(final Path file) {
        return NBTConfigurationLoader.builder().path(file).build();
    }

    @Override
    public NBTConfigurationLoader create(final Path file, final ConfigurationNode options) {
        return NBTConfigurationLoader.builder()
                .path(file)
                .editOptions(opts -> opts.values(nodeValueSource(options)))
                .build();
    }

    @Override
    public NBTConfigurationLoader create(final URL url) {
        return NBTConfigurationLoader.builder()
                .source(() -> new BufferedInputStream(url.openStream()))
                .build();
    }

    @Override
    public NBTConfigurationLoader create(final URL url, final ConfigurationNode options) {
        return NBTConfigurationLoader.builder()
                .source(() -> new BufferedInputStream(url.openStream()))
                .editOptions(opts -> opts.values(nodeValueSource(options)))
                .build();
    }

    private static ValueSource nodeValueSource(final ConfigurationNode node) {
        return new ValueSource() {
            @Override
            public @Nullable <T> T value(final Option<T> option) {
                try {
                    return node.node((Object[]) PATH_SPLIT.split(option.id(), -1)).get(option.valueType().type());
                } catch (final SerializationException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }
}
