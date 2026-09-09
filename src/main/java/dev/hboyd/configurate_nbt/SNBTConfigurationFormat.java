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

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationFormat;

import java.util.Set;

/**
 * The configuration format for the {@link SNBTConfigurationLoader}.
 */
public class SNBTConfigurationFormat extends AbstractConfigurationFormat<BasicConfigurationNode, SNBTConfigurationLoader, SNBTConfigurationLoader.Builder> {
    /**
     * For service loader only.
     */
    @ApiStatus.Internal
    public SNBTConfigurationFormat() {
        super("snbt", SNBTConfigurationLoader::builder, Set.of("snbt"));
    }
}
