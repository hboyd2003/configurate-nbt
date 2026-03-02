package dev.hboyd.configurateNBT;

import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationFormat;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.net.URL;
import java.nio.file.Path;
import java.util.Set;

@NullMarked
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
    public SNBTConfigurationLoader create(Path file, ConfigurationNode options) {
        return SNBTConfigurationLoader.builder().path(file).defaultOptions(options.options()).build();
    }

    /**
     * Unsupported by {@link SNBTConfigurationLoader}
     *
     * @throws UnsupportedOperationException for all calls
     */
    @Override
    public ConfigurationLoader<? extends Object> create(URL url, ConfigurationNode options) {
        throw new UnsupportedOperationException();
    }
}
