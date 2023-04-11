package ru.pantheon.filestorage

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import ru.pantheon.filestorage.local.LocalProperties

@Configuration
class PropertyConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties("storage.local")
    fun initLocalProps(): LocalProperties {
        return LocalProperties()
    }
}