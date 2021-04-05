package ru.pantheon.filestorage

/**
 * Поддерживаемые типы файловых хранилищ.
 *
 * @author Oleg Bryzhevatykh
 */
enum class StorageType {
    /**
     * Хранение файлов на диске.
     */
    LOCAL,

    /**
     * Хранение файлов в хранилище MINIO.
     */
    MINIO
}