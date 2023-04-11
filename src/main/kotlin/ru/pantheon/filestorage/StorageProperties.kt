package ru.pantheon.filestorage

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Primary

/**
 * Configuration properties for file storage support.
 *
 * @author Oleg Bryzhevatykh
 */
@Primary
@ConfigurationProperties(prefix = "storage")
open class StorageProperties {
    /**
     * Current file storage type.
     */
    var type: StorageType = StorageType.LOCAL
}
