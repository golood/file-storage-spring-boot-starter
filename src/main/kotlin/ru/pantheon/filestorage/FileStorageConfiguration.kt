package ru.pantheon.filestorage

import io.micrometer.core.instrument.MeterRegistry
import io.minio.BucketExistsArgs
import io.minio.MinioClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.annotation.EnableScheduling
import ru.pantheon.filestorage.actuator.MinioHealthIndicator
import ru.pantheon.filestorage.actuator.MinioPrometheusEndpoint
import ru.pantheon.filestorage.local.LocalFileStorage
import ru.pantheon.filestorage.local.LocalProperties
import ru.pantheon.filestorage.minio.MinioFileStorage
import ru.pantheon.filestorage.minio.MinioProperties

/**
 * Конфигурация реализаций файловых хранилищ.
 *
 * @author Oleg Bryzhevatykh
 */
@EnableScheduling
@EnableConfigurationProperties(StorageProperties::class, LocalProperties::class, MinioProperties::class)
@Configuration
class FileStorageConfiguration {

    /**
     * Инициализирует файловое хранилище для работы с локальными файлами сервера. Аннотация [Primary] используется для
     * подавления сообщения об ошибке невозможности выбора правильной реализации интерфейса в IDE.
     */
    @Primary
    @Bean
    @ConditionalOnProperty(value = ["storage.type"], havingValue = "local", matchIfMissing = true)
    fun initLocal(localProperties: LocalProperties): LocalFileStorage {
        return LocalFileStorage(localProperties)
    }

    /**
     * Инициализирует файловое хранилище для работы с S3 Minio.
     */
    @Bean
    @ConditionalOnProperty(value = ["storage.type"], havingValue = "minio", matchIfMissing = false)
    fun initMinio(minioProperties: MinioProperties, minioClient: MinioClient): MinioFileStorage {
        return MinioFileStorage(minioProperties, minioClient)
    }

    /**
     * Инициализирует Minio-клиент для бина [MinioFileStorage] и инициализируется только совместно с ним. При
     * инициализации подключается к файловому хранилищу и проверяет существование указанного в конфигурации bucket.
     */
    @Bean
    @ConditionalOnProperty(value = ["storage.type"], havingValue = "minio", matchIfMissing = false)
    fun minioClient(minioProperties: MinioProperties): MinioClient {
        val minioClient = MinioClient.builder()
            .endpoint(minioProperties.url)
            .credentials(minioProperties.accessKey, minioProperties.secretKey)
            .build()

        val bucketExistsArgs = BucketExistsArgs.builder()
            .bucket(minioProperties.bucket)
            .build()

        if (minioClient.bucketExists(bucketExistsArgs))
            return minioClient
        else
            throw Error("Minio bucket ${minioProperties.bucket} not found")
    }

    /**
     * Инициализирует бин [MinioHealthIndicator] для конфигурации системы, использующей [MinioFileStorage].
     */
    @ConditionalOnBean(MinioFileStorage::class)
    @Bean
    fun minioHealthIndicator(minioClient: MinioClient, minioProperties: MinioProperties) : MinioHealthIndicator {
        return MinioHealthIndicator(minioClient, minioProperties)
    }

    /**
     * Инициализирует бин сервиса [MinioPrometheusEndpoint] для конфигурации системы, использующей [MinioFileStorage].
     */
    @ConditionalOnBean(MinioFileStorage::class)
    @Bean
    fun minioPrometheusEndpoint(minioHealthIndicator: MinioHealthIndicator,
                                meterRegistry: MeterRegistry): MinioPrometheusEndpoint {
        return MinioPrometheusEndpoint(minioHealthIndicator, meterRegistry)
    }
}