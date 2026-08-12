package ru.rudikov.productmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Основной класс приложения Spring Boot.
 * Точка входа в приложение.
 */
@Slf4j
@EnableCaching
@SpringBootApplication
public class ProductManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductManagerApplication.class, args);
		log.info("Application started successfully!");
	}

}
