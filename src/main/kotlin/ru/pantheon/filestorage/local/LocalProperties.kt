package ru.pantheon.filestorage.local

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Component

/**
 * Конфигурация пути до ресурсов локального хранилища.
 *
 * @author Oleg Bryzhevatykh
 */
@ConfigurationProperties
open class LocalProperties(
    var path: String = "resources"
)
