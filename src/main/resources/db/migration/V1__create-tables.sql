-- SQL-скрипт для создания таблиц в базе данных
-- Таблицы создаются в порядке их зависимостей

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(15) NOT NULL UNIQUE,
                       password TEXT NOT NULL,
                       role TEXT NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       mobile_phone VARCHAR(15) NOT NULL,
                       is_enabled BOOLEAN DEFAULT false,
                       otp VARCHAR(6),
                       otp_generation_time TIMESTAMP
);

-- Опционально: можно добавить индексы для улучшения производительности запросов
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_mobile_phone ON users (mobile_phone);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          code VARCHAR(8) NOT NULL,
                          name VARCHAR(200) NOT NULL,
                          price DOUBLE PRECISION NOT NULL CHECK (price >= 0), -- Изменено на DOUBLE PRECISION
                          description VARCHAR(500),
                          entry_by BIGINT NULL,
                          entry_date TIMESTAMP,
                          updated_by BIGINT NULL,
                          updated_date TIMESTAMP -- Убрана запятая в конце
);

-- Опционально: можно добавить индексы для улучшения производительности запросов
CREATE INDEX idx_products_code ON products (code);
CREATE INDEX idx_products_name ON products (name);
