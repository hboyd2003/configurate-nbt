# Configurate NBT

[Configurate](https://github.com/SpongePowered/Configurate) loaders and serializers for NBT (Named Binary Tag) using [Kyori Adventure NBT](https://github.com/KyoriPowered/adventure).
Supports saving and loading via NBT as well as serializing NBT data into configure.

### Loaders
- NBT
- SNBT

### Serializers
This library provides BinaryTag serializers that can be used independently with any Configurate loader.
Since NBT needs to maintain type, two different kinds of serializers are provided:
- **Type-Safe Serializers** preserve exact numeric types by appending SNBT-style suffixes (`b`, `s`, `i`, `l`, `f`, `d`) to ensure correct NBT tag reconstruction across all formats.
- **Type-Unsafe Serializers** relies on the configuration loader to de-serialize to correct numeric type. Some loaders (HOCON, JSON, YAML, etc.) do not preserve type and as such will not de-serialize correctly.

For both kinds a static `TypeSerializerCollection` is accessible in `BinaryTagSerializer`

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven {
        name = "hboyd-dev-repo-releases"
        url = uri("https://repo.hboyd.dev/releases/")
    }
    
    // Required for snapshot releases
    maven {
        name = "hboyd-dev-repo-snapshots"
        url = uri("https://repo.hboyd.dev/snapshots/")
    }
}

dependencies {
    implementation("dev.hboyd:configurate-nbt:1.0.0")
}
```

## Examples

### Saving using SNBT

```java
// Create a loader for a SNBT file
SNBTConfigurationLoader loader = SNBTConfigurationLoader.builder()
    .path(Paths.get("config.snbt"))
    .indent(4)  // Indentation level
    .indentType(SNBTConfigurationLoader.IndentType.SPACE)
    .legacyFormat(false)
    .build();

// Load and save work the same as binary NBT
ConfigurationNode node = loader.load();
node.node("key").set("value");
loader.save(node);
```

### Saving an `ItemStack` in a YAML file with the Minecraft Paper API

```java
// Creat YAML loader
YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
        .defaultOptions(o ->
                o.serializers(o.serializers()
                    .childBuilder()
                    .registerAll(BinaryTagSerializer.TYPE_SAFE_SERIALIZERS)
                    .build()))
        .path("example.yml")
        .nodeStyle(NodeStyle.BLOCK)
        .build();

// Serialize ItemStack to NBT
CompoundBinaryTag itemStackNBT = inventory.getItemInMainHand().serializeAsBytes();

// Deserialize into Adventure's BinaryTag format
BinaryTag itemStackTag = BinaryTagIO.reader().read(new ByteArrayInputStream(), BinaryTagIO.Compression.GZIP);

// Save into Configurate node and save
ConfigurationNode itemStackTagNode = loader.createNode();
try {
        node.node("item").set(CompoundBinaryTag.class, itemStackTag);
        loader.save(node);
} catch (IOException e) {
        throw new RuntimeException(e);
}
```