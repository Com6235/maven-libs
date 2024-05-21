package io.github.com6235.configurator

import kotlinx.serialization.KSerializer
import java.io.InputStream

/**
 * Class for managing file extensions.
 * Use [addExtension] to add a pair of extension (just a file extension, like "json", "toml", "yaml") and a class,
 * that implements [Loader].
 *
 * @see ConfigLoader
 * @see Loader
 */
class FileExtensions<T : Any>(serializer: KSerializer<T>) {
    @JvmField
    val extensions: MutableMap<List<String>, Loader<T>> = mutableMapOf()
    
    private val defaults = mapOf(
        listOf("json") to JsonLoader(serializer),
        listOf("yaml") to YamlLoader(serializer),
        listOf("conf", "hocon") to HoconLoader(serializer),
        listOf("properties") to PropertiesLoader(serializer),
        listOf("toml") to TomlLoader(serializer),
    )

    init {
        extensions.putAll(defaults)
    }

    fun findExtension(extension: String): Loader<T>? =
        extensions.entries.firstOrNull { it.key.contains(extension.lowercase()) }?.value

    fun addExtension(extension: List<String>, loader: Loader<T>) {
        extensions[extension.map { it.lowercase() }] = loader
    }
}

/**
 *  Main class for creating custom loaders.
 *
 *  @see FileExtensions
 */
abstract class Loader<T : Any>(protected val serializer: KSerializer<T>) {
    abstract fun load(stream: InputStream): T
}
