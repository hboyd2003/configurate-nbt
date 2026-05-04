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

import dev.hboyd.configurateNBT.serializer.BinaryTagSerializer;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.option.Option;
import net.kyori.option.OptionSchema;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.loader.CommentHandler;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.UnmodifiableCollections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * Loads and saves {@link ConfigurationNode}s in the <a href="https://minecraft.wiki/w/NBT_format#SNBT_format">SNBT Format</a>.
 *
 * @see <a href="https://minecraft.wiki/w/NBT_format#SNBT_format">SNBT Format</a>
 */
public final class SNBTConfigurationLoader extends AbstractConfigurationLoader<BasicConfigurationNode> {
    private final TagStringIO tagStringIO;
    private @Nullable BinaryTag tag;

    private static final Set<Class<?>> NATIVE_TYPES = UnmodifiableCollections.toSet(
            Integer.class, Double.class, Byte.class, Long.class, Short.class, Float.class, // numeric
            int[].class, byte[].class, long[].class, String.class); // complex types

    private SNBTConfigurationLoader(final Builder builder) {
        super(builder, new CommentHandler[] {});

        final TagStringIO.Builder tagStringIOBuilder = TagStringIO.builder();

        if (builder.optionState().value(Builder.INDENT_TYPE) == IndentType.SPACE)
            tagStringIOBuilder.indent(builder.optionState().value(Builder.INDENT));
        else tagStringIOBuilder.indentTab(builder.optionState().value(Builder.INDENT));

        tagStringIOBuilder.acceptLegacy(builder.optionState().value(Builder.LEGACY_FORMAT));
        this.tagStringIO = tagStringIOBuilder.build();
    }

    /**
     * Attempts to save a {@link ConfigurationNode} using this loader, to the defined sink.
     *
     * @param node the node to save
     * @throws ConfigurateException if any sort of error occurs with writing or
     *                     generating the configuration
     */
    @Override
    public void save(final ConfigurationNode node) throws ConfigurateException {
        super.save(node);
    }

    /**
     * Create an empty node with the provided options.
     *
     * @param options node options
     * @return newly created empty node
     */
    @Override
    public BasicConfigurationNode createNode(final ConfigurationOptions options) {
        return BasicConfigurationNode.root(options);
    }

    @Override
    protected void loadInternal(final BasicConfigurationNode node, final BufferedReader reader) throws ParsingException {
        try {
            this.tag = this.tagStringIO.asCompound(reader.readAllAsString());
        } catch (final IOException e) {
            // Since StringTagParseException is not public we cannot provide correct exception
            throw new RuntimeException(e);
        }

        try {
            node.options().serializers().get(BinaryTag.class).serialize(BinaryTag.class, this.tag, node);
        } catch (final SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void saveInternal(final ConfigurationNode node, final Writer writer) throws ConfigurateException {
        this.tag = node.options().serializers().get(BinaryTag.class).deserialize(BinaryTag.class, node);
        try {
            this.tagStringIO.toWriter(this.tag, writer);
        } catch (final IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Create a new builder for {@link SNBTConfigurationLoader} instances.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Indent type.
     */
    public enum IndentType {
        /**
         * Indent using tab characters.
         */
        TAB,
        /**
         * Indent using space characters.
         */
        SPACE
    }

    /**
     * Builder for {@link SNBTConfigurationLoader} instances.
     */
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
