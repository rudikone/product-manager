# Sequence Diagrams — Product Manager Service

## Обзор архитектуры

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client                                  │
│                    (Browser / Mobile / API)                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SecurityFilterChain                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ SecurityFilter  │→ │  Authentication │  │  Authorization  │  │
│  │  (JWT Token)    │  │    Manager      │  │    Service      │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Controllers Layer                          │
│  ┌─────────────────────────┐  ┌─────────────────────────────┐   │
│  │ AuthenticationController│  │    ProductController        │   │
│  │  /auth/**               │  │     /products/**            │   │
│  └─────────────────────────┘  └─────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Services Layer                            │
│  ┌─────────────────────────┐  ┌─────────────────────────────┐   │
│  │ AuthenticationService   │  │    ProductService           │   │
│  │ AuthorizationService    │  │  (Cache + Validation)       │   │
│  │ TokenService            │  │                             │   │
│  └─────────────────────────┘  └─────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Repositories Layer                         │
│  ┌─────────────────────────┐  ┌─────────────────────────────┐   │
│  │   UserRepository        │  │    ProductRepository        │   │
│  │  (Spring Data JPA)      │  │   (Spring Data JPA)         │   │
│  └─────────────────────────┘  └─────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Database                                │
│                    (PostgreSQL via Flyway)                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## Сценарий 1: Регистрация пользователя (Signup)

```plantuml
@startuml
title Регистрация нового пользователя

participant "Client" as C
participant "AuthenticationController" as AC
participant "AuthenticationService" as AS
participant "UserRepository" as UR
participant "User" as U

C -> AC: POST /auth/signup\n{username, password, email, mobilePhone}
activate AC

AC -> AC: Validate @Valid SignupDTO

AC -> AS: signup(data)
activate AS

AS -> UR: existsByUsername(username)
activate UR
UR --> AS: boolean
deactivate UR

AS -> AS: Если true → throw UsernameAlreadyExistsException

AS -> UR: existsByEmail(email)
activate UR
UR --> AS: boolean
deactivate UR

AS -> AS: Если true → throw EmailAlreadyExistsException

AS -> AS: BCrypt.encode(password)

AS -> U: new User(username, encryptedPassword,\nemail, mobilePhone, role=USER)
activate U
U --> AS: User entity
deactivate U

AS -> UR: save(user)
activate UR
UR --> AS: saved User
deactivate UR

note right of AS
  ✅ Пользователь сразу активен
  (enabled = true по умолчанию)
end note

AS --> AC: void
deactivate AS

AC --> C: 200 OK\n"User successfully signed up"
deactivate AC

@enduml
```

**HTTP Request:**

```http
POST /auth/signup
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123!",
  "email": "john@example.com",
  "mobilePhone": "01234567890"
}
```

**Response:**

```http
HTTP/1.1 200 OK
Content-Type: text/plain

User successfully signed up
```

---

## Сценарий 2: Аутентификация (Login)

```plantuml
@startuml
title Аутентификация пользователя и получение JWT-токена

participant "Client" as C
participant "AuthenticationController" as AC
participant "AuthenticationService" as AS
participant "AuthenticationManager" as AM
participant "TokenService" as TS
participant "UserRepository" as UR
participant "SecurityContext" as SC

C -> AC: POST /auth/login\n{username, password}
activate AC

AC -> AC: Validate @Valid LoginDTO

AC -> AS: login(data)
activate AS

AS -> AM: authenticate(usernamePasswordToken)
activate AM

note right of AM
  Проверяет credentials через
  UserDetailsService.loadUserByUsername()
end note

AM -> AM: Если AuthenticationException → throw InvalidCredentialsException

AM --> AS: Authentication (User principal)
deactivate AM

AS -> TS: generateToken(User)
activate TS

TS -> TS: JWT.create(\n  issuer="auth-service",\n  subject=username,\n  expiresAt=now+EXPIRATION_TIME\n)

TS --> AS: JWT token string
deactivate TS

AS --> AC: LoginResponseDTO(token)
deactivate AS

AC --> C: 200 OK\n{"token": "eyJhbGc..."}
deactivate AC

note right of C
  Клиент сохраняет токен
  и использует для всех запросов
  в заголовке: Authorization: Bearer {token}
end note

@enduml
```

**HTTP Request:**

```http
POST /auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123!"
}
```

**Response:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## Сценарий 3: Получение продуктов с пагинацией

```plantuml
@startuml
title Получение списка продуктов с пагинацией и сортировкой

participant "Client" as C
participant "SecurityFilter" as SF
participant "ProductController" as PC
participant "ProductService" as PS
participant "ProductRepository" as PR
participant "Cache" as Cache

note over C
  Запрос БЕЗ токена
  (GET /products/** разрешён всем)
end note

C -> SF: GET /products/paginated?page=0&size=10&sort=name,asc
activate SF

SF -> SF: Извлечь токен из Authorization header
SF -> SF: Если токен есть → validate и установить SecurityContext

SF -> PC: findAll(page, size, sort)
activate SF
activate PC

PC -> PS: getWithPagination(page, size, sort)
activate PS

PS -> PS: Validate:\n- page >= 0, size >= 0\n- sort direction: asc/desc\n- size <= 60

PS -> Cache: Check cache(key=page+size+sort)
activate Cache
Cache --> PS: Cached Page or MISS
deactivate Cache

alt Cache MISS
    PS -> PR: findAll(Pageable)
    activate PR
    PR --> PS: Page<Product>
    deactivate PR

    PS -> PS: Если !hasNext → throw ProductsEmptyException

    PS -> Cache: Put result in cache
end

PS -> PS: Map Page<Product> → Page<ProductDTO>

PS --> PC: Iterable<ProductDTO>
deactivate PS

PC --> C: 200 OK\n[{"code":"P001","name":"Product A",...}]
deactivate PC
deactivate SF

@enduml
```

**HTTP Request:**

```http
GET /products/paginated?page=0&size=10&sort=name,asc
```

**Response:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "code": "P001",
    "name": "Product Alpha",
    "price": 99.99,
    "description": "Description here"
  },
  ...
]
```

---

## Сценарий 4: Создание продукта (Требует ADMIN)

**HTTP Request:**

```http
POST /products/create
Authorization: Bearer eyJhbGc...
Content-Type: application/json

{
  "code": "P002",
  "name": "Product Beta",
  "price": 149.99,
  "description": "New product description"
}
```

**Response:**

```http
HTTP/1.1 200 OK
Content-Type: text/plain

Product successfully created
```

---

## Сценарий 5: Получение продукта по ID

**HTTP Request:**

```http
GET /products/find?product=123
```

**Response:**

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "code": "P001",
  "name": "Product Alpha",
  "price": 99.99,
  "description": "Description"
}
```

---

## Сценарий 6: Обновление продукта (ADMIN only)

**HTTP Request:**

```http
PUT /products/update?product=123
Authorization: Bearer eyJhbGc...
Content-Type: application/json

{
  "code": "P002-UPD",
  "name": "Product Beta Updated",
  "price": 199.99,
  "description": "Updated description"
}
```

**Response:**

```http
HTTP/1.1 200 OK
Content-Type: text/plain

Product successfully updated
```

---

## Сценарий 7: Удаление продукта (ADMIN only)

**HTTP Request:**

```http
DELETE /products/delete?product=123
Authorization: Bearer eyJhbGc...
```

**Response:**

```http
HTTP/1.1 200 OK
Content-Type: text/plain

Product successfully deleted
```

---

## Сценарий 8: JWT Token Validation Flow (SecurityFilter)

```plantuml
@startuml
title JWT Token Validation в SecurityFilter

participant "Client" as C
participant "SecurityFilter" as SF
participant "TokenService" as TS
participant "UserRepository" as UR
participant "SecurityContext" as SC
participant "FilterChain" as FC

C -> SF: HTTP Request\nAuthorization: Bearer {token}
activate SF

SF -> SF: recoverToken(request)

alt Token is null/empty/not Bearer
    SF -> FC: doFilter(request, response)
    activate FC
    FC --> SF: Continue chain
    deactivate FC
else Token present
    SF -> TS: validateToken(token)
    activate TS

    TS -> TS: JWT.require().verify(token)

    alt Token invalid
        TS --> SF: "" (empty string)
    else Token valid
        TS --> SF: username (subject)
    end
    deactivate TS

    SF -> UR: findByUsername(username)
    activate UR
    UR --> SF: UserDetails
    deactivate UR

    SF -> SC: setAuthentication(\n  UsernamePasswordAuthenticationToken(\n    user, null, authorities\n  ))
end

SF -> FC: doFilter(request, response)
activate FC
FC --> SF: Request processed by controller
deactivate FC

SF --> C: Response
deactivate SF

@enduml
```

---

## Таблица endpoint'ов

| Метод  | Endpoint              | Auth | Role  | Описание                        |
|--------|-----------------------|------|-------|---------------------------------|
| POST   | `/auth/signup`        | ❌    | -     | Регистрация нового пользователя |
| POST   | `/auth/login`         | ❌    | -     | Аутентификация, получение JWT   |
| GET    | `/products/paginated` | ❌    | -     | Получение списка продуктов      |
| GET    | `/products/find`      | ❌    | -     | Получение продукта по ID        |
| POST   | `/products/create`    | ✅    | ADMIN | Создание продукта               |
| PUT    | `/products/update`    | ✅    | ADMIN | Обновление продукта             |
| DELETE | `/products/delete`    | ✅    | ADMIN | Удаление продукта               |
| GET    | `/swagger-ui/**`      | ❌    | -     | Swagger UI                      |
| GET    | `/v3/api-docs/**`     | ❌    | -     | OpenAPI spec                    |

---

## Кэш

**Cache Provider:** Spring Cache (по умолчанию ConcurrentMapCache)

| Cache Name | Key                                        | Evict Trigger                               |
|------------|--------------------------------------------|---------------------------------------------|
| `products` | `page.toString() + size.toString() + sort` | createProduct, updateProduct, deleteProduct |
| `products` | `productId`                                | createProduct, updateProduct, deleteProduct |

---

## Исключения

### Auth Exceptions

| Exception                        | HTTP Status | Condition              |
|----------------------------------|-------------|------------------------|
| `UsernameAlreadyExistsException` | 400         | Username занят         |
| `EmailAlreadyExistsException`    | 400         | Email занят            |
| `InvalidCredentialsException`    | 401         | Неверный логин/пароль  |
| `UserNotFoundException`          | 404         | Пользователь не найден |

### Product Exceptions

| Exception                               | HTTP Status | Condition                  |
|-----------------------------------------|-------------|----------------------------|
| `ProductNotFoundException`              | 404         | Продукт не найден          |
| `ProductsEmptyException`                | 404         | Страница пуста             |
| `ModelValidationException`              | 400         | Code/Name уже существует   |
| `InvalidArgumentsToPaginationException` | 400         | page < 0 или size < 0      |
| `InvalidSortDirectionException`         | 400         | sort direction не asc/desc |

---

## Примечания

### ✅ Упрощения (OTP и Mail удалены)

Следующие endpoint'ы **удалены** как избыточные:

- ~~`POST /auth/verify-account`~~ — подтверждение аккаунта через OTP
- ~~`POST /auth/resend-verification`~~ — повторная отправка OTP
- ~~`POST /password/request-reset`~~ — запрос сброса пароля
- ~~`POST /password/reset`~~ — сброс пароля

**Пользователь активен сразу после регистрации** — поле `enabled` всегда `true`.

### 🔐 Безопасность

- JWT токены используются для stateless аутентификации
- Время жизни токена настраивается в `application.properties`: `auth.security.token.expiration-time`
- Пароли хранятся в encrypted виде (BCrypt)
- Только ADMIN может создавать/обновлять/удалять продукты
- GET запросы к продуктам доступны без аутентификации

### 📦 Удалённые классы

**Mail-функционал:**

- `EmailUtil`
- `EmailConfigurations`
- `PasswordResetController`
- `PasswordResetService`
- `IPasswordResetService`

**OTP-функционал:**

- `OneTimePassword`
- `OtpUtil`
- `SignupResponseDTO`

**Исключения:**

- `UserAlreadyVerifiedException`
- `UserNotEnabledException`
- `InvalidOtpException`
- `MissingArgumentsToResetPasswordException`
- `PasswordsDoNotMatchException`
- `ResetPasswordExceptionsHandler`
