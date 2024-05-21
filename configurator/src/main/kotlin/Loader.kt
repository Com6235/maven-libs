package io.github.com6235.configurator

import kotlinx.serialization.KSerializer
import java.io.*
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory

/**
 * Loader for configs, add custom loaders using [FileExtensions.addExtension].
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
    val fileExtensions = FileExtensions(serializer)

    fun loadConfig(stream: InputStream, fileExtension: String): T {
        val loader = fileExtensions.findExtension(fileExtension) ?: throw NotSerializableException()
        return loader.load(stream)
    }

    fun loadConfig(path: Path): T? {
        if (!path.exists() || path.isDirectory()) return null
        return loadConfig(path.inputStream(), path.extension)
    }
}
