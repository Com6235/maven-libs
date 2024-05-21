package io.github.com6235.configurator

import kotlinx.serialization.KSerializer
import java.io.*
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory

class ConfigLoader<T : Any>(serializer: KSerializer<T>) {
    val fileExtensions = FileExtensions(serializer)

    fun loadConfig(stream: InputStream, fileExtension: String): Config<T> {
        val loader = fileExtensions.findExtension(fileExtension) ?: throw NotSerializableException()
        return Config(loader.load(stream))
    }

    fun loadConfig(path: Path): Config<T>? {
        if (!path.exists() || path.isDirectory()) return null
        return loadConfig(path.inputStream(), path.extension)
    }
}
