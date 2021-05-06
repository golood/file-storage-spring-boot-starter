package ru.pantheon.filestorage

import org.springframework.core.io.Resource
import java.io.InputStream

/**
 * Интерфейс для взаимодействия с файлами системы.
 *
 * @author Oleg Bryzhevatykh
 */
interface FileStorage {

    /**
     * Выполняет проверку доступности директории с файлами системы для пространства [spaceId]. При необходимости,
     * пытается создать нужный путь.
     */
    fun validatePath(spaceId: Long)

    /**
     * Возвращает ресурс аватарки пользователя.
     */
    fun getAvatar(name: String): Resource

    /**
     * Загружает в хранилище аватарку пользователя.
     */
    fun uploadAvatar(resource: Resource, name: String)

    /**
     * Удаляет их хранилища аватарку пользователя.
     */
    fun removeAvatar(name: String)

    /**
     * Возвращает файловый ресурс пространства [spaceId] по имени [name].
     */
    fun getResource(spaceId: Long, name: String): Resource

    /**
     * Возвращает поток байт ресурса пространства [spaceId] по имени [name]. Извлекаются байты со смещением [offset], в
     * количестве [length].
     */
    fun getStream(spaceId: Long, name: String, offset: Long, length: Long): InputStream

    /**
     * Загружает [resource] в пространство [spaceId]. При передаче [name], сохраняет под данным именем, иначе имя
     * берётся из ресурса.
     */
    fun uploadResource(spaceId: Long, resource: Resource, name: String? = null)

    /**
     * Удаляет файловый ресурс пространства [spaceId] по имени [name].
     */
    fun removeResource(spaceId: Long, name: String): Boolean

    /**
     * Возвращает размер файла в пространстве [spaceId] по имени [name] в байтах.
     */
    fun getResourceSize(spaceId: Long, name: String): Long
}