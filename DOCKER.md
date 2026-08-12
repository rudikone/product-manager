# Запуск через Docker Compose

## Быстрый старт

### 1. Запуск инфраструктуры

```bash
# Запустить только PostgreSQL
docker-compose up -d postgres

# Запустить всё (включая pgAdmin и MailHog)
docker-compose --profile tools up -d
```

### 2. Проверка статуса

```bash
docker-compose ps
```

### 3. Остановка

```bash
# Остановить только PostgreSQL
docker-compose down

# Остановить всё
docker-compose --profile tools down
```

---

## Компоненты

| Сервис     | Порт                   | Описание                                         |
|------------|------------------------|--------------------------------------------------|
| `postgres` | 5432                   | База данных PostgreSQL 15                        |
| `pgadmin`  | 5050                   | Web-интерфейс для управления БД (профиль: tools) |
| `mailhog`  | 1025 (SMTP), 8025 (UI) | Перехват email для разработки (профиль: tools)   |

---

## Доступы

### PostgreSQL

- **Хост:** `localhost`
- **Порт:** `5432`
- **База данных:** `yourdatabase`
- **Пользователь:** `postgres`
- **Пароль:** `postgres_password` (из `.env`)

### pgAdmin

- **URL:** http://localhost:5050
- **Email:** `admin@example.com`
- **Пароль:** `admin_password` (из `.env`)

### MailHog Web UI

- **URL:** http://localhost:8025

---

## Запуск приложения

После запуска PostgreSQL:

```bash
mvn spring-boot:run
```

Или через IDE — запустить `CleanSpringBootProjectApplication`.

---

## Переменные окружения

Все секреты хранятся в файле `.env` (добавлен в `.gitignore`):

```bash
JWT_SECRET=ваш-секрет-минимум-32-символа
DB_PASSWORD=пароль_БД
```

---

## Полезные команды

```bash
# Посмотреть логи
docker-compose logs -f postgres

# Очистить данные БД (удалит volume!)
docker-compose down -v

# Пересоздать контейнеры
docker-compose up -d --force-recreate
```
