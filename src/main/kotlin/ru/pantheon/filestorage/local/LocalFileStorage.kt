package ru.pantheon.filestorage.local

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.util.FileCopyUtils
import org.springframework.util.StreamUtils
import ru.pantheon.filestorage.FileStorage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * Реализация [FileStorage] для работы с локальными файлами сервера.
 *
 * @author Oleg Bryzhevatykh
 */
class LocalFileStorage(localProperties: LocalProperties) : FileStorage {
    private val basePath: String
    private val avatarPath: String

    init {
        val baseDirectory = File(localProperties.path)
        basePath = baseDirectory.absolutePath

        val avatarDirectory = File("$basePath/avatar")
        avatarPath = avatarDirectory.absolutePath

        // Проверяем существование пути до папки с ресурсами
        if (!baseDirectory.exists() || !baseDirectory.isDirectory)
            throw Error("Unavailable resource directory $basePath")

        // Проверяем возможность записи в папку
        if (!baseDirectory.canWrite())
            throw Error("Access denied to resource directory $basePath")

        // Проверяем наличие папки с аватарками пользователей или создаём её
        if (!avatarDirectory.exists() && !avatarDirectory.mkdirs())
            throw Error("Access denied to avatar directory $avatarPath")
    }

    override fun validatePath(spaceId: Long) {
        val spaceDirectory = File("$basePath/space/${spaceId}/upload")
        if (!spaceDirectory.exists() && !spaceDirectory.mkdirs())
            throw Error("Unavailable resource directory ${spaceDirectory.absolutePath}")
    }

    override fun getAvatar(name: String): Resource {
        return FileSystemResource("$avatarPath/$name")
    }

    override fun uploadAvatar(resource: Resource, name: String) {
        StreamUtils.copy(resource.inputStream, FileOutputStream("$avatarPath/$name"))
    }

    override fun removeAvatar(name: String) {
        File("$avatarPath/$name").delete()
    }

    override fun getResource(spaceId: Long, name: String): Resource {
        return FileSystemResource("$basePath/space/$spaceId/upload/$name")
    }

    override fun getStream(spaceId: Long, name: String, offset: Long, length: Long): InputStream {
        val randomAccessFile = RandomAccessFile("$basePath/space/$spaceId/upload/$name", "r")

        val byteArray = ByteArray(length.toInt())
        randomAccessFile.seek(offset)
        randomAccessFile.read(byteArray, 0, length.toInt())
        return ByteArrayResource(byteArray).inputStream
    }

    override fun uploadResource(spaceId: Long, resource: Resource, name: String?) {
        val fileName = name ?: resource.filename
        FileCopyUtils.copy(resource.inputStream, FileOutputStream("$basePath/space/$spaceId/upload/$fileName"))
    }

    override fun removeResource(spaceId: Long, name: String): Boolean {
        return File("$basePath/space/$spaceId/upload/$name").delete()
    }
}