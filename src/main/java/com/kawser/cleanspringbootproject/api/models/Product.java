package com.kawser.cleanspringbootproject.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность продукта.
 * Каждый продукт имеет id, код, имя, цену и описание.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Products")
@Builder
public class Product extends Base<Long> {

    /*
     * Код продукта. Не может быть пустым.
     */
    @NotBlank(message = "Product code cannot be blank")
    @Size(min = 4, max = 8, message = "Code must be between 4 and 8 characters long")
    private String code;

    /*
     * Название продукта. Не может быть пустым.
     */
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters long")
    private String name;

    /*
     * Цена продукта. Не может быть меньше нуля.
     */
    @Min(value = 0, message = "Price cannot be less than zero")
    private Double price;

    /*
     * Описание продукта. Не может быть пустым.
     */
    @Size(max = 500, message = "Description must be 500 characters or less")
    private String description;

}
