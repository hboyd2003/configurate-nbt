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

package dev.hboyd.configurateNBT;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationFormat;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.net.URL;
import java.nio.file.Path;
import java.util.Set;

/**
 * The configuration format for the {@link SNBTConfigurationLoader}.
 */
public class SNBTConfigurationFormat implements ConfigurationFormat {
    @Override
    public String id() {
        return "snbt";
    }

    @Override
    public Set<String> supportedExtensions() {
        return Set.of("snbt");
    }

    /**
     * Create a new {@link SNBTConfigurationLoader} configured to load from the provided file.
     *
     * @param file the file to load from
     * @param options the options to use to configure the node
     * @return a newly created {@link SNBTConfigurationLoader} loader
     */
    @Override
    public SNBTConfigurationLoader create(final Path file, final ConfigurationNode options) {
        return SNBTConfigurationLoader.builder().path(file).defaultOptions(options.options()).build();
    }

    /**
     * Unsupported by {@link SNBTConfigurationLoader}.
     *
     * @throws UnsupportedOperationException for all calls
     */
    @Override
    public ConfigurationLoader<? extends Object> create(final URL url, final ConfigurationNode options) {
        throw new UnsupportedOperationException();
    }
}
