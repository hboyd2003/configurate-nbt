package dev.hboyd.configurateNBT;

import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationFormat;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Set;

@NullMarked
public class NBTConfigurationFormat implements ConfigurationFormat {
    @Override
    public String id() {
        return "nbt";
    }

    @Override
    public Set<String> supportedExtensions() {
        return Set.of("nbt", "dat");
    }

    /**
     * Create a new {@link NBTConfigurationLoader} configured to load from the provided file.
     *
     * @param file the file to load from
     * @param options the options to use to configure the node
     * @return a newly created {@link NBTConfigurationLoader} loader
     */
    @Override
    public NBTConfigurationLoader create(Path file, ConfigurationNode options) {
        try {
            return NBTConfigurationLoader.builder().path(file).defaultOptions(options.options()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unsupported by {@link NBTConfigurationLoader}
     *
     * @throws UnsupportedOperationException for all calls
     */
    @Override
    public ConfigurationLoader<? extends Object> create(URL url, ConfigurationNode options) {
        throw new UnsupportedOperationException();
    }
}
