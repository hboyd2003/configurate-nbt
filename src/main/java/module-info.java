/**
 * <a href="https://github.com/SpongePowered/Configurate">Configurate</a> loaders and serializers for NBT
 * (Named Binary Tag) using <a href="https://github.com/KyoriPowered/adventure">Kyori Adventure NBT</a>.
 *
 * @provides org.spongepowered.configurate.loader.ConfigurationFormat
 */
module dev.hboyd.configurateNBT {
    requires transitive net.kyori.adventure;
    requires transitive net.kyori.adventure.nbt;
    requires transitive net.kyori.option;
    requires transitive net.kyori.examination.api;
    requires transitive org.spongepowered.configurate;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
    requires static org.checkerframework.checker.qual;

    exports dev.hboyd.configurateNBT;
    exports dev.hboyd.configurateNBT.serializer;

    provides org.spongepowered.configurate.loader.ConfigurationFormat with
            dev.hboyd.configurateNBT.NBTConfigurationFormat,
            dev.hboyd.configurateNBT.SNBTConfigurationFormat;
}
