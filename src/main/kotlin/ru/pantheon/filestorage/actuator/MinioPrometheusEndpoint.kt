package ru.pantheon.filestorage.actuator

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.health.Status
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.atomic.AtomicInteger

/**
 * Сервис выгрузки метрик из [MinioHealthIndicator] в [MeterRegistry] прометея.
 *
 * @author Oleg Bryzhevatykh
 */
class MinioPrometheusEndpoint(private val minioHealthIndicator: MinioHealthIndicator,
                              meterRegistry: MeterRegistry) {

    private val minioStatus: AtomicInteger? = meterRegistry.gauge("app_minio_status", AtomicInteger(0))!!

    @Scheduled(fixedDelay = 10000, initialDelay = 5000)
    fun updateMinioStatus() {
        if (Status.UP == minioHealthIndicator.health().status) minioStatus?.set(1) else minioStatus?.set(0)
    }
}