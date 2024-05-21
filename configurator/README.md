# Configirator

Thing to automatically load config from different formats. Supports custom formats. 
Uses [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).

Currently, it supports 
[JSON](https://github.com/Kotlin/kotlinx.serialization/blob/master/formats/README.md#JSON) (`json`), 
[HOCON](https://github.com/Kotlin/kotlinx.serialization/blob/master/formats/README.md#HOCON) (`conf`, `hocon`), 
[TOML](https://github.com/Peanuuutz/tomlkt) (`toml`), 
[YAML](https://github.com/charleskorn/kaml) (`yaml`) and
[Properties](https://github.com/Kotlin/kotlinx.serialization/blob/master/formats/README.md#properties) (`properties`)

Latest version: https://github.com/Com6235/maven-libs/packages/2156249

## Using as a dependency
### Maven

After you added the repository, you can start using my package by adding this to your `pom.xml`
```xml
<dependency>
    <groupId>io.github.com6235</groupId>
    <artifactId>configurator</artifactId>
    <version>${your desired version}</version>
</dependency>
```

### Gradle

After you added the repository, you can start using my package by adding this to `dependencies`:

In `build.gradle.kts`:
```kotlin
dependencies {
    implementation("io.github.com6235:configurator:${your desired version}")
}
```

In `build.gradle`:
```groovy
dependencies {
    implementation 'io.github.com6235:configurator:${your desired version}'
}
```

## Examples

### Print some info from resources

```kotlin
import io.github.com6235.configurator.ConfigLoader
import kotlinx.serialization.Serializable

@Serializable
data class Config(val helpPages: List<String>)

fun main() {
    val configStream = this::class.java.getResourceAsStream("config.json")!!
    val config = ConfigLoader(Config.serializer()).loadConfig(configStream, "json") // change 
    
    println(config.helpPages)
}
```