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

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationFormat;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Set;

/**
 * The configuration format for the {@link NBTConfigurationLoader}.
 */
public class NBTConfigurationFormat implements ConfigurationFormat {
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
        return NBTConfigurationLoader.builder().path(file).defaultOptions(options.options()).build();
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
                .defaultOptions(options.options())
                .build();
    }
}
