package io.github.com6235.configurator

import kotlinx.serialization.KSerializer
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.NotSerializableException
import java.nio.file.Path
import kotlin.io.path.*

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
     * Loads a config from string.
     *
     * @param string The string with data
     * @param fileFormat Format of data for selecting the data loader
     */
    fun loadConfig(string: String, fileFormat: String): T {
        val loader = fileFormats.findFormat(fileFormat) ?: throw NotSerializableException()
        return loader.load(ByteArrayInputStream(string.toByteArray()))
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

    /**
     * Serialize a config into a string.
     *
     * @param data Data to serialize
     * @param format Format to serialize the data into
     * @return Serialized string
     */
    fun saveConfig(data: T, format: String): String {
        val loader = fileFormats.findFormat(format) ?: throw NotSerializableException()
        return loader.save(data)
    }

    /**
     * Save a config into a file.
     *
     * @param data Data to serialize
     * @param path Path of the file
     */
    fun saveConfig(data: T, path: Path) {
        if (path.isDirectory()) return
        if (!path.exists()) {
            path.createFile()
        }
        val serialized = saveConfig(data, path.extension)
        path.writeText(serialized)
    }
}
