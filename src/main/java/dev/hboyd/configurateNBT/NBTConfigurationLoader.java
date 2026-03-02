package dev.hboyd.configurateNBT;

import dev.hboyd.configurateNBT.serializer.BinaryTagSerializer;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.option.Option;
import net.kyori.option.OptionSchema;
import net.kyori.option.OptionState;
import net.kyori.option.value.ValueSource;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.*;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.WatchServiceListener;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.util.UnmodifiableCollections;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class NBTConfigurationLoader implements ConfigurationLoader<BasicConfigurationNode> {
    private static final Set<Class<?>> NATIVE_TYPES = UnmodifiableCollections.toSet(
            Integer.class, Double.class, Byte.class, Long.class, Short.class, Float.class, // numeric
            int[].class, byte[].class, long[].class, String.class); // complex types


    private final @Nullable Callable<BufferedInputStream> source;
    private final @Nullable Callable<BufferedOutputStream> sink;
    private final ConfigurationOptions defaultOptions;
    private final BinaryTagIO.Compression compression;

    private NBTConfigurationLoader(Builder builder) {
        this.source = builder.source();
        this.sink = builder.sink();
        this.defaultOptions = builder.defaultOptions();
        
        this.compression = builder.optionState().value(Builder.COMPRESSION).compression();
    }

    /**
     * Attempts to load a {@link ConfigurationNode} using this loader, from the defined source.
     *
     * <p>The resultant node represents the root of the configuration being
     * loaded.</p>
     *
     * @param options the options to load with
     * @return the newly constructed node
     * @throws ConfigurateException if any sort of error occurs with reading or
     *                              parsing the configuration
     */
    @Override
    public BasicConfigurationNode load(ConfigurationOptions options) throws ConfigurateException {
        BufferedInputStream inputStream;
        try {
            inputStream = source.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return load(options, inputStream);
    }

    /**
     * Attempts to load a {@link ConfigurationNode} using this loader, using a given byte array as the source.
     *
     * <p>The resultant node represents the root of the configuration being
     * loaded.</p>
     *
     * @param input the byte array to load with
     * @return the newly constructed node
     * @throws ConfigurateException if any sort of error occurs with reading or
     *                              parsing the configuration
     */
    public BasicConfigurationNode loadFromBytes(final byte[] input) throws ConfigurateException {
        return loadFromBytes(defaultOptions, input);
    }

    /**
     * Attempts to load a {@link ConfigurationNode} using this loader, using a given byte array as the source.
     *
     * <p>The resultant node represents the root of the configuration being
     * loaded.</p>
     *
     * @param options the options to load with
     * @param input the byte array to load with
     * @return the newly constructed node
     * @throws ConfigurateException if any sort of error occurs with reading or
     *                              parsing the configuration
     */
    public BasicConfigurationNode loadFromBytes(ConfigurationOptions options, final byte[] input) throws ConfigurateException {
        return load(options, new BufferedInputStream(new ByteArrayInputStream(input)));
    }

    private BasicConfigurationNode load(ConfigurationOptions options, BufferedInputStream inputStream) throws ConfigurateException {
        BasicConfigurationNode node = createNode(options);

        try {
            final CompoundBinaryTag tag = BinaryTagIO.unlimitedReader().read(inputStream, compression);

            final TypeSerializer<CompoundBinaryTag> serializer
                    = requireNonNull(node.options().serializers().get(CompoundBinaryTag.class), "CompoundBinaryTag serializer");
            serializer.serialize(CompoundBinaryTag.class, tag, node);
        } catch (FileNotFoundException | NoSuchFileException _) {
        } catch (IOException e) {
            throw new ConfigurateException(e);
        }

        return node;
    }

    /**
     * Attempts to load data from the defined source into a {@link ConfigurationReference}.
     * The returned reference will not reload automatically.
     *
     * @return the created reference
     * @throws ConfigurateException when an error occurs within the loader
     * @see WatchServiceListener#listenToConfiguration(Function, Path) to
     *      create an auto-reloading configuration.
     */
    @Override
    public ConfigurationReference<BasicConfigurationNode> loadToReference() throws ConfigurateException {
        return ConfigurationReference.fixed(this);
    }


    /**
     * Attempts to save a {@link ConfigurationNode} using this loader, to the defined sink.
     *
     * @param node the node to save
     * @throws ConfigurateException if any sort of error occurs with writing or
     *                     generating the configuration
     */
    @Override
    public void save(ConfigurationNode node) throws ConfigurateException {
        BufferedOutputStream outputStream;
        try {
            outputStream = sink.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        save(node, outputStream, compression);
    }

    /**
     * Attempts to save a {@link ConfigurationNode} using this loader, to a returned byte array.
     *
     * @param node the node to save
     * @return the saved {@link ConfigurationNode} as a byte array
     * @throws ConfigurateException if any sort of error occurs with writing or
     *                     generating the configuration
     */
    public byte[] saveToBytes(ConfigurationNode node) throws ConfigurateException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        save(node, new BufferedOutputStream(byteArrayOutputStream), compression);
        return byteArrayOutputStream.toByteArray();
    }

    private static void save(ConfigurationNode node, BufferedOutputStream outputStream, BinaryTagIO.Compression compression) throws ConfigurateException {
        final TypeSerializer<CompoundBinaryTag> serializer
                = requireNonNull(node.options().serializers().get(CompoundBinaryTag.class), "CompoundBinaryTag serializer");
        final CompoundBinaryTag tag = serializer.deserialize(CompoundBinaryTag.class, node);

        try {
            BinaryTagIO.writer().write(tag, outputStream, compression);
            outputStream.close();
        } catch (IOException e) {
            throw new ConfigurateException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create an empty node with the provided options.
     *
     * @param options node options
     * @return newly created empty node
     */
    @Override
    public BasicConfigurationNode createNode(ConfigurationOptions options) {
        return BasicConfigurationNode.root(options);
    }

    /**
     * Default options for the types of nodes created by this loader.
     *
     * @return default options
     */
    @Override
    public ConfigurationOptions defaultOptions() {
        return this.defaultOptions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private static final String CONFIGURATE_PREFIX = "configurate";

        private static final OptionSchema.Mutable UNSAFE_SCHEMA = OptionSchema.emptySchema();
        private static final OptionSchema SCHEMA = UNSAFE_SCHEMA.frozenView();

        public static final Option<NBTCompression> COMPRESSION = UNSAFE_SCHEMA.enumOption("nbt:compression", NBTCompression.class, NBTCompression.GZIP);

        private OptionState.Builder optionBuilder;
        private @Nullable OptionState optionState;
        private ConfigurationOptions defaultOptions;

        private Callable<BufferedInputStream> source;
        private Callable<BufferedOutputStream> sink;

        private Builder() {
            this.defaultOptions = ConfigurationOptions.defaults()
                    .serializers(TypeSerializerCollection.defaults().childBuilder()
                            .registerAll(BinaryTagSerializer.TYPE_SAFE_SERIALIZERS).build())
                    .nativeTypes(NATIVE_TYPES);
            this.optionBuilder = SCHEMA.stateBuilder()
                    .values(ValueSource.systemProperty(CONFIGURATE_PREFIX))
                    .values(ValueSource.environmentVariable(CONFIGURATE_PREFIX));
            this.optionState = null;
        }

        /**
         * Compute a snapshot of the currently set options for created loaders.
         *
         * @return the option state
         */
        public OptionState optionState() {
            if (this.optionState == null)
                this.optionState = this.optionBuilder.build();

            return this.optionState;
        }

            public Builder optionState(final OptionState state) {
            this.optionBuilder = SCHEMA.stateBuilder()
                    .values(state);
            this.optionState = null;

            return this;
        }

        /**
         * Modify the state of loader options set on this loader.
         *
         * @param builderConsumer a consumer that receives the modifier to
         *                        perform changes
         * @return this builder
         */
            public Builder editOptions(final Consumer<OptionState.Builder> builderConsumer) {
            this.optionState = null;
            builderConsumer.accept(this.optionBuilder);
            return this;
        }

        /**
         * Sets the sink and source of the resultant loader to the given file.
         *
         * <p>The {@link #source() source} is defined using {@link Files#newInputStream(Path, OpenOption...)}</p>
         *
         * <p>The {@link #sink() sink} is defined using {@link Files#newOutputStream(Path, OpenOption...)}</p>
         *
         * @param file the configuration file
         * @return this builder (for chaining)
         */
            public Builder file(final File file) throws IOException {
            return path(requireNonNull(file, "file").toPath());
        }

        /**
         * Sets the sink and source of the resultant loader to the given path.
         *
         * @param path the path of the configuration file
         * @return this builder (for chaining)
         */
            public Builder path(final Path path) throws IOException {
            final Path absPath = requireNonNull(path, "path").toAbsolutePath();
            this.source = () -> new BufferedInputStream(Files.newInputStream(absPath));
            this.sink = () -> new BufferedOutputStream(Files.newOutputStream(absPath));
            return this;
        }
        
        /**
         * Sets the source of the resultant loader.
         *
         * <p>The "source" is used by the loader to load the configuration.</p>
         *
         * @param source the source
         * @return this builder (for chaining)
         */
            public Builder source(final Callable<BufferedInputStream> source) {
            this.source = source;
            return this;
        }

        /**
         * Gets the source to be used by the resultant loader.
         *
         * <p>Can be null (for loaders which only support saving with {@link NBTConfigurationLoader#saveToBytes(ConfigurationNode)})</p>
         * 
         * @return the source
         */
        public @Nullable Callable<BufferedInputStream> source() {
            return this.source;
        }

        /**
         * Sets the sink of the resultant loader.
         *
         * <p>The "sink" is used by the loader to save the configuration.</p>
         *
         * @param sink the sink
         * @return this builder (for chaining)
         */
            public Builder sink(final Callable<BufferedOutputStream> sink) {
            this.sink = sink;
            return this;
        }

        /**
         * Gets the sink to be used by the resultant loader.
         *
         * <p>Can be null (for loaders which only support loading with {@link NBTConfigurationLoader#loadFromBytes(byte[])})</p>
         * 
         * @return the sink
         */
        public @Nullable Callable<BufferedOutputStream> sink() {
            return this.sink;
        }

        /**
         * Sets the default configuration options to be used by the
         * resultant loader.
         *
         * @param defaultOptions the options
         * @return this builder (for chaining)
         */
            public Builder defaultOptions(final ConfigurationOptions defaultOptions) {
            this.defaultOptions = requireNonNull(defaultOptions, "defaultOptions");
            return this;
        }

        /**
         * Sets the default configuration options to be used by the resultant
         * loader by providing a function which takes the current default
         * options and applies any desired changes.
         *
         * @param defaultOptions to transform the existing default options
         * @return this builder (for chaining)
         */
            public Builder defaultOptions(final UnaryOperator<ConfigurationOptions> defaultOptions) {
            this.defaultOptions = requireNonNull(defaultOptions.apply(this.defaultOptions), "defaultOptions (updated)");
            return this;
        }

        /**
         * Sets the compression that will be used by the resultant loader.
         *
         * @param compressionType type of compression to use
         * @return this builder (for chaining)
         */
            public Builder compression(final NBTCompression compressionType) {
            this.optionBuilder.value(COMPRESSION, compressionType);
            return this;
        }

        /**
         * Gets the default configuration options to be used by the resultant
         * loader.
         *
         * @return the options
         */
        public ConfigurationOptions defaultOptions() {
            return this.defaultOptions;
        }

        /**
         * Builds the loader.
         *
         * @return a new loader
         */
        public NBTConfigurationLoader build() {
            return new NBTConfigurationLoader(this);
        }

        /**
         * Configure to read from a byte array, build, and load in one step.
         *
         * @param input the input to load
         * @return a deserialized node
         */
            public ConfigurationNode buildAndLoadByteArray(final byte[] input) throws ConfigurateException {
            return this.source(() -> new BufferedInputStream(new ByteArrayInputStream(input)))
                    .build()
                    .load();
        }

        /**
         * Configure to write to a byte array, build, and save in one step.
         *
         * @param output the node to write
         * @return the output byte array
         */
            public byte[] buildAndSaveByteArray(final ConfigurationNode output) throws ConfigurateException {
            requireNonNull(output, "output");
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            this.sink(() -> new BufferedOutputStream(outputStream))
                    .build()
                    .save(output);
            return outputStream.toByteArray();
        }
    }
}
