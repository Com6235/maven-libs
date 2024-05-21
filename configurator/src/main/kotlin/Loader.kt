package io.github.com6235.configurator

import kotlinx.serialization.KSerializer
import java.io.*
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory

/**
 * Loader for configs, add custom loaders using [FileFormats.addFormat].
 * Use [loadConfig] to load configs.
 * For this to work, you must have a data class with `@Serializable` annotation:
 * ```
 * @Serializable
 * data class Human(val name: String)
 *
 * ConfigLoader(Human.serializer()).loadConfig(Path("./config.json"))
 * ```
 *
 * @see Loader
 */
class ConfigLoader<T : Any>(serializer: KSerializer<T>) {
    val fileFormats = FileFormats(serializer)

    /**
     * Loads a config from stream.
     *
     * @param stream The stream with data
     * @param fileFormat Format of data for selecting the data loader
     */
    fun loadConfig(stream: InputStream, fileFormat: String): T {
        val loader = fileFormats.findFormat(fileFormat) ?: throw NotSerializableException()
        return loader.load(stream)
    }

    /**
     * Loads a config from a file.
     *
     * @param path Path of the file
     * @return `null` if the path doesn't exist or is a directory. Else - deserialized data
     */
    fun loadConfig(path: Path): T? {
        if (!path.exists() || path.isDirectory()) return null
        return loadConfig(path.inputStream(), path.extension)
    }
}
