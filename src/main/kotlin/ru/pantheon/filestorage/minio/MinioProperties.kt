package ru.pantheon.filestorage.minio

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * Конфигурация подключения к Minio S3.
 *
 * @author Oleg Bryzhevatykh
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "storage.minio")
open class MinioProperties {

    var url: String = "http://localhost:9000"
    var bucket: String = "minervakms"
    var accessKey: String = "minioadmin"
    var secretKey: String = "minioadmin"
}