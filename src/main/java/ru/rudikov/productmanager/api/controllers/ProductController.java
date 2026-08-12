package ru.rudikov.productmanager.api.controllers;

import ru.rudikov.productmanager.api.models.Product;
import ru.rudikov.productmanager.api.models.dto.ProductDTO;
import ru.rudikov.productmanager.api.services.IProductService;
import ru.rudikov.productmanager.api.services.impl.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Контроллер для управления продуктами.
 * Обрабатывает HTTP-запросы, связанные с продуктами.
 * Использует ProductService для выполнения операций с базой данных.
 *
 * @see ProductService
 * @see Product
 */
@RestController
@ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "Нет прав доступа к этому ресурсу"),
        @ApiResponse(responseCode = "404", description = "Продукт не найден"),
})
@RequestMapping("/products")
public class ProductController {

    private final IProductService productService;

    public ProductController(@Qualifier("standard") IProductService productService) {
        this.productService = productService;
    }

    private final ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    /**
     * Возвращает все продукты с пагинацией и сортировкой по указанным полям.
     * @param page Номер страницы для возврата.
     * @param size Количество элементов на странице. По умолчанию 10, максимум 60.
     * @param sort Массив строк в формате "field,direction" для сортировки. По умолчанию "name,asc".
     * @return Список найденных продуктов.
     */
    @Operation(summary = "Найти все продукты с пагинацией",
            description = "Найти все продукты с пагинацией. Максимальный размер страницы: 60.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукты найдены"),
            @ApiResponse(responseCode = "400", description = "Неверные аргументы пагинации"),
            @ApiResponse(responseCode = "404", description = "Продукты не найдены")
    })
    @GetMapping("/paginated")
    public ResponseEntity<Iterable<ProductDTO>> findAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name,asc") String[] sort) {
        Iterable<ProductDTO> entities = productService.getWithPagination(page, size, sort);
        return ResponseEntity.ok(entities);
    }

    /**
     * Создаёт новый продукт с указанными данными.
     * @param productDTO DTO продукта для создания, передаётся в теле запроса.
     * @return Сообщение об успешном создании продукта.
     */
    @Operation(summary = "Создать новый продукт", description = "Создать новый продукт с указанными данными")
    @ApiResponse(responseCode = "200", description = "Продукт создан")
    @PostMapping("/create")
    public ResponseEntity<String> save(
            @RequestBody @Valid ProductDTO productDTO) {

        productService.createProduct(productDTO);
        return ResponseEntity.ok(bundle.getString("product.successfully_created"));
    }

    /**
     * Возвращает продукт по его ID.
     * @param productId ID продукта для поиска, передаётся как параметр запроса.
     * @return DTO найденного продукта.
     */
    @Operation(summary = "Найти продукт по ID", description = "Найти продукт по его ID")
    @ApiResponse(responseCode = "200", description = "Продукт найден")
    @GetMapping("/find")
    public ResponseEntity<Optional<ProductDTO>> findById(
            @RequestParam(name = "product") Long productId) {

        Optional<ProductDTO> product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    /**
     * Обновляет продукт с указанными новыми данными.
     * @param productId ID продукта для обновления, передаётся как параметр запроса.
     * @param productUpdated DTO продукта с новыми данными, передаётся в теле запроса.
     * @return Сообщение об успешном обновлении продукта.
     */
    @Operation(summary = "Обновить продукт", description = "Обновить продукт с указанными данными")
    @ApiResponse(responseCode = "200", description = "Продукт обновлён")
    @PutMapping("/update")
    public ResponseEntity<String> update(
            @RequestParam(name = "product") Long productId,
            @RequestBody @Valid ProductDTO productUpdated) {

        productService.updateProduct(productId, productUpdated);
        return ResponseEntity.ok(bundle.getString("product.successfully_updated"));
    }

    /**
     * Удаляет продукт по его ID.
     * @param productId ID продукта для удаления, передаётся как параметр запроса.
     * @return Сообщение об успешном удалении продукта.
     */
    @Operation(summary = "Удалить продукт", description = "Удалить продукт по его ID")
    @ApiResponse(responseCode = "200", description = "Продукт удалён")
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(
            @RequestParam(name = "product") Long productId) {

        productService.deleteProduct(productId);
        return ResponseEntity.ok(bundle.getString("product.successfully_deleted"));
    }
}
