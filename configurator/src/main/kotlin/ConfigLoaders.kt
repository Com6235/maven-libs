package io.github.com6235.configurator

import com.charleskorn.kaml.Yaml
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.peanuuutz.tomlkt.Toml
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

internal class YamlLoader<T : Any>(serializer: KSerializer<T>) : Loader<T>(serializer) {
    override fun load(stream: InputStream): T =
        yaml.decodeFromString(serializer, stream.readBytes().toString(Charset.defaultCharset()))

    companion object {
        val yaml = Yaml()
    }
}

internal class HoconLoader<T : Any>(serializer: KSerializer<T>) : Loader<T>( serializer) {
    @OptIn(ExperimentalSerializationApi::class)
    override fun load(stream: InputStream): T {
        val conf = ConfigFactory.parseString(
            stream.readBytes().toString(Charset.defaultCharset()),
            ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF)
        )
        return Hocon.decodeFromConfig(serializer, conf)
    }
}

internal class PropertiesLoader<T : Any>(serializer: KSerializer<T>) : Loader<T>(serializer) {
    @OptIn(ExperimentalSerializationApi::class)
    override fun load(stream: InputStream): T {
        val properties = Properties()
        properties.load(stream)
        val map: Map<String, String> = properties.toMap().mapKeys { it.key.toString() }.mapValues { it.value.toString() }
        return kotlinx.serialization.properties.Properties.decodeFromMap(serializer, map)
    }
}

internal class JsonLoader<T : Any>(serializer: KSerializer<T>) : Loader<T>(serializer) {
    @OptIn(ExperimentalSerializationApi::class)
    override fun load(stream: InputStream): T = json.decodeFromStream(serializer, stream)

    companion object {
        val json = Json {
            prettyPrint = true
        }
    }
}

internal class TomlLoader<T : Any>(serializer: KSerializer<T>) : Loader<T>(serializer) {
    override fun load(stream: InputStream): T =
        Toml.decodeFromString(serializer, stream.readBytes().toString(Charset.defaultCharset()))
}