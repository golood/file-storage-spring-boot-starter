package ru.pantheon.filestorage.actuator

import io.minio.BucketExistsArgs
import io.minio.MinioClient
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import ru.pantheon.filestorage.minio.MinioProperties
import java.net.ConnectException

/**
 * Предоставляет Minio [HealthIndicator] для Actuator.
 *
 * @author Oleg Bryzhevatykh
 */
class MinioHealthIndicator(private val minioClient: MinioClient,
                           minioProperties: MinioProperties) : HealthIndicator {

    private val args = BucketExistsArgs.builder().bucket(minioProperties.bucket).build()

    private val healthUp = Health.up().build()
    private val healthDown = Health.down().build()

    override fun health(): Health {
        return try {
            if (minioClient.bucketExists(args)) healthUp else healthDown
        } catch (exception: ConnectException) {
            return healthDown
        }
    }
}
