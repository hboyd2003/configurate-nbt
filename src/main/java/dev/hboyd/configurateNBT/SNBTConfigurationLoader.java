package dev.hboyd.configurateNBT;

import dev.hboyd.configurateNBT.serializer.BinaryTagSerializer;
import net.kyori.adventure.nbt.*;
import net.kyori.option.Option;
import net.kyori.option.OptionSchema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.loader.*;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.UnmodifiableCollections;

import java.io.*;
import java.util.*;

@NullMarked
public final class SNBTConfigurationLoader extends AbstractConfigurationLoader<BasicConfigurationNode> {
    private final TagStringIO tagStringIO;
    private @Nullable BinaryTag tag;

    private static final Set<Class<?>> NATIVE_TYPES = UnmodifiableCollections.toSet(
            Integer.class, Double.class, Byte.class, Long.class, Short.class, Float.class, // numeric
            int[].class, byte[].class, long[].class, String.class); // complex types

    private SNBTConfigurationLoader(Builder builder) {
        super(builder, new CommentHandler[] {});

        TagStringIO.Builder tagStringIOBuilder = TagStringIO.builder();

        if (builder.optionState().value(Builder.INDENT_TYPE) == IndentType.SPACE)
            tagStringIOBuilder.indent(builder.optionState().value(Builder.INDENT));
        else tagStringIOBuilder.indentTab(builder.optionState().value(Builder.INDENT));

        tagStringIOBuilder.acceptLegacy(builder.optionState().value(Builder.LEGACY_FORMAT));
        this.tagStringIO = tagStringIOBuilder.build();
    }

    public enum IndentType {
        TAB,
        SPACE
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
        super.save(node);
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

    @Override
    protected void loadInternal(BasicConfigurationNode node, BufferedReader reader) throws ParsingException {
        try {
            this.tag = this.tagStringIO.asCompound(reader.readAllAsString());
        } catch (IOException e) {
            // Since StringTagParseException is not public we cannot provide correct exception
            throw new RuntimeException(e);
        }

        try {
            node.options().serializers().get(BinaryTag.class).serialize(BinaryTag.class, this.tag, node);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void saveInternal(ConfigurationNode node, Writer writer) throws ConfigurateException {
        this.tag = node.options().serializers().get(BinaryTag.class).deserialize(BinaryTag.class, node);
        try {
            tagStringIO.toWriter(this.tag, writer);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends AbstractConfigurationLoader.Builder<Builder, SNBTConfigurationLoader> {
        private static final OptionSchema.Mutable UNSAFE_SCHEMA = OptionSchema.childSchema(AbstractConfigurationLoader.Builder.SCHEMA);

        public static final OptionSchema SCHEMA = UNSAFE_SCHEMA.frozenView();

        public static final Option<IndentType> INDENT_TYPE = UNSAFE_SCHEMA.enumOption("snbt:indent_style", IndentType.class, IndentType.SPACE);

        public static final Option<Integer> INDENT = UNSAFE_SCHEMA.intOption("snbt:indent", 4);

        public static final Option<Boolean> LEGACY_FORMAT = UNSAFE_SCHEMA.booleanOption("snbt:legacy_format", false);

        Builder() {
            this.defaultOptions = this.defaultOptions()
                    .nativeTypes(NATIVE_TYPES)
                    .serializers(this.defaultOptions.serializers().childBuilder()
                            .registerAll((BinaryTagSerializer.TYPE_UNSAFE_SERIALIZERS))
                            .build());
        }

        @Override
        protected OptionSchema optionSchema() {
            return SCHEMA;
        }

        /**
         * Sets the indent type the resultant loader should use.
         *
         * @param indentType the type of indent
         * @return this builder (for chaining)
         */
        public Builder indentType(final IndentType indentType) {
            this.optionStateBuilder().value(INDENT_TYPE, indentType);
            return this;
        }

        /**
         * Sets the level of indentation the resultant loader should use.
         * Set to 0 to disable pretty printing
         *
         * @param indent the indent level
         * @return this builder (for chaining)
         */
        public Builder indent(final int indent) {
            this.optionStateBuilder().value(INDENT, indent);
            return this;
        }

        /**
         * Sets if the resultant loader reads and outputs in legacy SNBT format.
         *
         * @param legacyFormat to use legacy formating
         * @return this builder (for chaining)
         */
        public Builder legacyFormat(final boolean legacyFormat) {
            this.optionStateBuilder().value(LEGACY_FORMAT, legacyFormat);
            return this;
        }

        @Override
        public SNBTConfigurationLoader build() {
            return new SNBTConfigurationLoader(this);
        }
    }
}
