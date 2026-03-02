package dev.hboyd.configurateNBT;

import net.kyori.adventure.nbt.BinaryTagIO;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum NBTCompression {
    NONE(BinaryTagIO.Compression.NONE),
    GZIP(BinaryTagIO.Compression.GZIP),
    ZLIB(BinaryTagIO.Compression.ZLIB);

    private final BinaryTagIO.Compression compression;

    NBTCompression(BinaryTagIO.Compression compression) {
        this.compression = compression;
    }

    public BinaryTagIO.Compression compression() {
        return compression;
    }
}
