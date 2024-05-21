package io.github.com6235.configurator

import kotlinx.serialization.KSerializer
import java.io.InputStream

/**
 * Class for managing data formats.
 * Use [addFormat] to add a pair of a format (just a file extension, like "json", "toml", "yaml") and a class,
 * that implements [Loader].
 *
 * @see ConfigLoader
 * @see Loader
 */
class FileFormats<T : Any>(serializer: KSerializer<T>) {
    @JvmField
    val formats: MutableMap<List<String>, Loader<T>> = mutableMapOf()
    
    private val defaults = mapOf(
        listOf("json") to JsonLoader(serializer),
        listOf("yaml") to YamlLoader(serializer),
        listOf("conf", "hocon") to HoconLoader(serializer),
        listOf("properties") to PropertiesLoader(serializer),
        listOf("toml") to TomlLoader(serializer),
    )

    init {
        formats.putAll(defaults)
    }

    fun findFormat(extension: String): Loader<T>? =
        formats.entries.firstOrNull { it.key.contains(extension.lowercase()) }?.value

    fun addFormat(extension: List<String>, loader: Loader<T>) {
        formats[extension.map { it.lowercase() }] = loader
    }
}

/**
 *  Main class for creating custom loaders.
 *
 *  @see FileFormats
 */
abstract class Loader<T : Any>(protected val serializer: KSerializer<T>) {
    /**
     * Function, that deserializes the data into the given type [T]
     */
    abstract fun load(stream: InputStream): T

    /**
     * Function, that serializes the data in type [T] into a String
     */
    abstract fun save(data: T): String
}
