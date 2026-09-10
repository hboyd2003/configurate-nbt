/**
 * <a href="https://github.com/SpongePowered/Configurate">Configurate</a> loaders and serializers for NBT
 * (Named Binary Tag) using <a href="https://github.com/KyoriPowered/adventure">Kyori Adventure NBT</a>.
 *
 * @provides org.spongepowered.configurate.loader.ConfigurationFormat
 */
module dev.hboyd.configurate_nbt {
    requires transitive net.kyori.adventure.api;
    requires transitive net.kyori.adventure.nbt;
    requires transitive net.kyori.option;
    requires transitive org.spongepowered.configurate;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
    requires static org.checkerframework.checker.qual;

    exports dev.hboyd.configurate_nbt;
    exports dev.hboyd.configurate_nbt.serializer;

    provides org.spongepowered.configurate.loader.ConfigurationFormat with
            dev.hboyd.configurate_nbt.NBTConfigurationFormat,
            dev.hboyd.configurate_nbt.SNBTConfigurationFormat;
}
