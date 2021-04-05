# File Storage Spring Boot Starter

Spring Boot Starter предоставляющий механизмы работы с файловыми хранилищами для чтения, записи и удаления файлов. 
Предоставляет две реализации хранения файлов. Ресурсы разделяются на группы по назначению. Отдельно хранятся аватарки 
пользователей и отдельно коллекции файлов, разделённых на пространства.

* LocalFileStorage - реализация для работы с файлами на диске сервера
* MinioFileStorage - реализация для работы с файлами в хранилище Minio S3

Maven
```xml
<dependency>
    <groupId>ru.pantheon</groupId>
    <artifactId>file-storage-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle
```groovy
implementation 'ru.pantheon:file-storage-spring-boot-starter:1.0.0'
```

Переключение типа файлового хранилища осуществляется в конфигурации:
```properties
# Тип файлового хранилища [local | minio]
storage.type=local
```

Для типа хранилища **local** требуется переопределить путь до корневой папки ресурсов:
```properties
storage.local.path=resources
```

Для типа хранилища **minio** требуется переопределить следующие параметры:
```properties
storage.minio.url=http://localhost:9000
storage.minio.bucket=minervakms
storage.minio.accessKey=minioadmin
storage.minio.secretKey=minioadmin
```

## Примеры
Стартер предоставляет реализации интерфейсов **FileStorage** для унификации взаимодействия с ресурасами в любом типе 
хранилища. Для удобвства работы в экосистеме спринга, большая часть методов использует интерфейс 
*org.springframework.core.io.Resource*.

```kotlin
@RestController
@RequestMapping("/{spaceId}")
class TestController {

    @Autowired
    lateinit var fileStorage: FileStorage

    @GetMapping("/{fileName}")
    fun getFile(@PathVariable spaceId: Long, @PathVariable fileName: String): Resource {
        return fileStorage.getResource(spaceId, fileName)
    }
}
```

Перед тем, как использовать файловое хранилище, рекомендуется произвести предварительную проверку наличия папок или
выполнить их создание. Для этого нужно на старте системы или в процессе работы вызвать метод 
**FileStorage#validatePath** для требуемых пространств системы.
```kotlin
@PostConstruct
fun init() {
    fileStorage.validatePath(1L)
}
```

# Actuator & Prometheus
Стартер добавляет индикатор работоспособности приложения, показывающий состояние соединения при использовании хранилища 
Minio. Если корзина была удалена или отсутствует соедиение с Minio, будет возвращён статус **DOWN**.

Для прометея предоставляется метрика **app_minio_status** предоставляющая значение 0 или 1, являющееся 
индикатором доступности Minio.
