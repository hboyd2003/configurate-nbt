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

import net.kyori.adventure.nbt.BinaryTagIO;

/**
 * NBT compression types.
 */
public enum NBTCompression {
    /**
     * No compression.
     */
    NONE(BinaryTagIO.Compression.NONE),
    /**
     * <a href="https://en.wikipedia.org/wiki/Gzip">GZIP</a> compression.
     */
    GZIP(BinaryTagIO.Compression.GZIP),
    /**
     * <a href="https://en.wikipedia.org/wiki/Zlib">ZLIB</a> compression.
     */
    ZLIB(BinaryTagIO.Compression.ZLIB);

    private final BinaryTagIO.Compression compression;

    NBTCompression(final BinaryTagIO.Compression compression) {
        this.compression = compression;
    }

    /**
     * Gets the underlying {@link BinaryTagIO.Compression}.
     *
     * @return the compression
     */
    public BinaryTagIO.Compression compression() {
        return this.compression;
    }
}
