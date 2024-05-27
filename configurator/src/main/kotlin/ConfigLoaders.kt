package io.github.com6235.configurator

import com.charleskorn.kaml.Yaml
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigSyntax
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtVariant
import net.benwoodworth.knbt.decodeFromStream
import net.peanuuutz.tomlkt.Toml
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

class YamlLoader<T : Any>(
    serializer: KSerializer<T>,
    private val yaml: Yaml = Yaml()
) : Loader<T>(serializer) {
    override fun load(stream: InputStream): T =
        yaml.decodeFromString(serializer, stream.readBytes().toString(Charset.defaultCharset()))

    override fun save(data: T): String = yaml.encodeToString(serializer, data)

}

@OptIn(ExperimentalSerializationApi::class)
class HoconLoader<T : Any>(
    serializer: KSerializer<T>,
    private val hocon: Hocon = Hocon
) : Loader<T>( serializer) {
    override fun load(stream: InputStream): T {
        val conf = ConfigFactory.parseString(
            stream.readBytes().toString(Charset.defaultCharset()),
            ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
        )
        return hocon.decodeFromConfig(serializer, conf)
    }

    override fun save(data: T): String {
        val renderOptions = ConfigRenderOptions.defaults().setOriginComments(false).setJson(false).setFormatted(true)
        val serialized = Hocon.encodeToConfig(serializer, data).resolve().root().render(renderOptions)
        return serialized
    }
}

class PropertiesLoader<T : Any>(serializer: KSerializer<T>) : Loader<T>(serializer) {
    @OptIn(ExperimentalSerializationApi::class)
    override fun load(stream: InputStream): T {
        val properties = Properties()
        properties.load(stream)
        val map: Map<String, String> = properties.toMap().mapKeys { it.key.toString() }.mapValues { it.value.toString() }
        return kotlinx.serialization.properties.Properties.decodeFromMap(serializer, map)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun save(data: T): String {
        val serialized = kotlinx.serialization.properties.Properties.encodeToStringMap(serializer, data)
        return serialized.map { (k, v) -> "$k=$v" }.joinToString("\n")
    }
}

class JsonLoader<T : Any>(
    serializer: KSerializer<T>,
    private val json: Json = Json { prettyPrint = true }
) : Loader<T>(serializer) {
    @OptIn(ExperimentalSerializationApi::class)
    override fun load(stream: InputStream): T = json.decodeFromStream(serializer, stream)

    override fun save(data: T): String = json.encodeToString(serializer, data)
}

class TomlLoader<T : Any>(serializer: KSerializer<T>) : Loader<T>(serializer) {
    override fun load(stream: InputStream): T =
        Toml.decodeFromString(serializer, stream.readBytes().toString(Charset.defaultCharset()))

    override fun save(data: T): String = Toml.encodeToString(serializer, data)
}

class NbtLoader<T : Any>(
    serializer: KSerializer<T>,
    private val nbt: Nbt = Nbt { compression = NbtCompression.None; variant = NbtVariant.Java }
) : Loader<T>(serializer) {
    override fun load(stream: InputStream): T = nbt.decodeFromStream(serializer, stream)

    override fun save(data: T): String = nbt.encodeToByteArray(serializer, data).toString(Charset.defaultCharset())
}
