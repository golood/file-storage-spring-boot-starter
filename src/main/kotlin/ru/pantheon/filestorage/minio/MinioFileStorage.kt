package ru.pantheon.filestorage.minio

import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.StatObjectArgs
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import ru.pantheon.filestorage.FileStorage
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Реализация [FileStorage] для работы с хранилищем данных S3 Minio.
 *
 * @author Oleg Bryzhevatykh
 */
class MinioFileStorage(minioProperties: MinioProperties,
                       private val minioClient: MinioClient) : FileStorage {

    companion object {
        /**
         * Размер пакета при загрузке файлов в Minio. По-умолчанию 5Mb.
         */
        private const val PART_SIZE: Long = 5 * 1024 * 1024
    }

    private val bucket: String = minioProperties.bucket

    init {
        val args = PutObjectArgs.builder()
            .bucket(bucket)
            .`object`("avatar/")
            .stream(ByteArrayInputStream(ByteArray(0)), 0, -1)
            .build()
        minioClient.putObject(args)
    }

    override fun validatePath(spaceId: Long) {
        val args = PutObjectArgs.builder()
            .bucket(bucket)
            .`object`("space/$spaceId/upload/")
            .stream(ByteArrayInputStream(ByteArray(0)), 0, -1)
            .build()

        minioClient.putObject(args)
    }

    override fun getAvatar(name: String): Resource {
        val args = GetObjectArgs.builder()
            .bucket(bucket)
            .`object`("avatar/$name")
            .build()

        return InputStreamResource(minioClient.getObject(args))
    }

    override fun uploadAvatar(resource: Resource, name: String) {
        val args = PutObjectArgs.builder()
            .bucket(bucket)
            .`object`("avatar/$name")
            .stream(resource.inputStream, -1, PART_SIZE)
            .build()

        minioClient.putObject(args)
    }

    override fun removeAvatar(name: String) {
        val args = RemoveObjectArgs.builder()
            .bucket(bucket)
            .`object`("avatar/$name")
            .build()

        minioClient.removeObject(args)
    }

    override fun getResource(spaceId: Long, name: String): Resource {
        val args = GetObjectArgs.builder()
            .bucket(bucket)
            .`object`("space/$spaceId/upload/$name")
            .build()

        return InputStreamResource(minioClient.getObject(args))
    }

    override fun getStream(spaceId: Long, name: String, offset: Long, length: Long): InputStream {
        val args = GetObjectArgs.builder()
            .bucket(bucket)
            .`object`("space/$spaceId/upload/$name")
            .offset(offset)
            .length(length)
            .build()

        return minioClient.getObject(args)
    }

    override fun uploadResource(spaceId: Long, resource: Resource, name: String?) {
        val fileName = name ?: resource.filename
        val args = PutObjectArgs.builder()
            .bucket(bucket)
            .`object`("space/$spaceId/upload/$fileName")
            .stream(resource.inputStream, -1, PART_SIZE)
            .build()

        minioClient.putObject(args)
    }

    override fun removeResource(spaceId: Long, name: String): Boolean {
        val args = RemoveObjectArgs.builder()
            .bucket(bucket)
            .`object`("space/$spaceId/upload/$name")
            .build()

        minioClient.removeObject(args)
        return true
    }

    override fun getResourceSize(spaceId: Long, name: String): Long {
        val args = StatObjectArgs.builder()
            .bucket(bucket)
            .`object`("space/$spaceId/upload/$name")
            .build()

        return minioClient.statObject(args).size()
    }
}