package ru.rudikov.productmanager.api.repositories;

import ru.rudikov.productmanager.api.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCode(String code);

    boolean existsByName(String name);

}   
