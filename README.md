## Возможности

- **Аутентификация пользователей**
    - Регистрация с подтверждением по email
    - Вход с аутентификацией на основе JWT
    - Функция сброса пароля

- **Управление продуктами**
    - CRUD-операции (создание, чтение, обновление, удаление)
    - Пагинация списка продуктов
    - Кэширование для улучшения производительности

- **Дополнительные функции**
    - Подробное логирование
    - Модульные тесты
    - OpenAPI документация (Swagger UI)
    - Миграция базы данных с помощью Flyway
    - Email-уведомления

## Структура проекта

```
my-spring-boot-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── kawser/
│   │   │           └── cleanspringbootproject/
│   │   │               ├── api/
│   │   │               │   ├── controllers/
│   │   │               │   ├── models/
│   │   │               │   │   └── dto/
│   │   │               │   ├── repositories/
│   │   │               │   ├── services/
│   │   │               │   │   └── impl/
│   │   │               ├── auth/
│   │   │               │   ├── config/
│   │   │               │   ├── controllers/
│   │   │               │   ├── models/
│   │   │               │   │   └── dto/
│   │   │               │   ├── repositories/
│   │   │               │   ├── services/
│   │   │               │   │   └── impl/
│   │   │               │   ├── util/
│   │   │               ├── exception/
│   │   │               │   ├── api/
│   │   │               │   ├── auth/
│   │   │               │   ├── global.handler/
│   │   │               └── CleanSpringBootProjectApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   ├── schema.sql
│   │   │   └── messages.properties
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── kawser/
│       │           └── cleanspringbootproject/
│       │               ├── api/
│       │               │   ├── controllers/
│       │               │   ├── repositories/
│       │               │   ├── services/
│       │               ├── auth/
│       │               │   ├── controllers/
│       │               │   ├── repositories/
│       │               │   ├── services/
│       │               └── CleanSpringBootProjectApplicationTests.java
│       └── resources/
│           └── application-test.properties
├── .gitignore
├── README.md
└── pom.xml
```

## Зависимости

- **Spring Boot Starter Data JPA**
    - Предоставляет интеграцию с Spring Data JPA для доступа к базе данных и ORM.

- **Spring Boot Starter Security**
    - Добавляет функции безопасности, такие как аутентификация и авторизация, с помощью Spring
      Security.

- **Spring Boot Starter Web**
    - Создание веб-приложений, включая RESTful-сервисы, с использованием Spring MVC.

- **Spring Boot Starter Mail**
    - Поддержка отправки email с помощью JavaMailSender.

- **Spring Boot DevTools**
    - Улучшает процесс разработки с помощью таких функций, как автоматический перезапуск и live
      reload.

- **Flyway Core**
    - Предоставляет контроль версий схемы базы данных для миграций.

- **PostgreSQL JDBC Driver**
    - Позволяет приложению подключаться к базам данных PostgreSQL.

- **Java JWT**
    - Библиотека для создания и проверки JSON Web Tokens (JWT) для аутентификации.

- **Lombok**
    - Уменьшает количество шаблонного кода за счёт генерации геттеров, сеттеров и других методов во
      время компиляции.

- **Spring Boot Actuator**
    - Добавляет в приложение производственные функции, такие как мониторинг и метрики.

- **SpringDoc OpenAPI**
    - Интегрирует спецификацию OpenAPI 3 с Spring Boot, предоставляя UI для документации API.

- **Spring Boot Starter Validation**
    - Поддерживает валидацию пользовательского ввода с помощью аннотаций JSR-303/JSR-380.

- **Spring Boot Starter Cache**
    - Предоставляет поддержку кэширования для улучшения производительности приложения.

- **SLF4J и Log4j**
    - Фреймворк логирования, поддерживающий различные реализации.

- **Java Faker**
    - Библиотека для генерации тестовых данных, таких как имена, адреса и номера телефонов.

- **Spring Boot Starter Test**
    - Включает тестовые библиотеки, такие как JUnit, Hamcrest и Mockito, для модульного и
      интеграционного тестирования.

- **Spring Security Test**
    - Предоставляет поддержку тестирования функций безопасности Spring.

- **Testcontainers**
    - Позволяет тестировать с помощью контейнеризированных зависимостей, таких как базы данных и
      веб-серверы.

- **H2 Database**
    - Встроенная база данных в памяти, используемая для тестирования.

## Начало работы

### Необходимые условия

- Java 21
- Maven 3.6+
- PostgreSQL

### Установка

1. Клонируйте репозиторий:

   ```sh
   git clone https://github.com/yourusername/clean-spring-boot-project.git
   cd clean-spring-boot-project
   ```

2. Обновите настройки базы данных в `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/yourdatabase
   spring.datasource.username=yourusername
   spring.datasource.password=yourpassword

   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   spring.mail.host=smtp.example.com
   spring.mail.port=587
   spring.mail.username=your-email@example.com
   spring.mail.password=your-email-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true

   jwt.secret=your-jwt-secret
   ```

3. Запустите приложение:

   ```sh
   mvn spring-boot:run
   ```

- **Документация API доступна по адресу http://localhost:8080/swagger-ui/index.html после запуска
  приложения.**
    - Она предоставляет подробную информацию о доступных эндпоинтах, их параметрах запроса и
      форматах ответов.