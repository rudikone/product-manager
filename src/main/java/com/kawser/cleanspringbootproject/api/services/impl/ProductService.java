package com.kawser.cleanspringbootproject.api.services.impl;

import com.kawser.cleanspringbootproject.api.models.Product;
import com.kawser.cleanspringbootproject.api.models.dto.ProductDTO;
import com.kawser.cleanspringbootproject.api.repositories.ProductRepository;
import com.kawser.cleanspringbootproject.api.services.IProductService;
import com.kawser.cleanspringbootproject.auth.services.IAuthorizationService;
import com.kawser.cleanspringbootproject.exception.api.domain.common.ModelValidationException;
import com.kawser.cleanspringbootproject.exception.api.domain.pagination.InvalidArgumentsToPaginationException;
import com.kawser.cleanspringbootproject.exception.api.domain.pagination.InvalidSortDirectionException;
import com.kawser.cleanspringbootproject.exception.api.domain.product.ProductNotFoundException;
import com.kawser.cleanspringbootproject.exception.api.domain.product.ProductsEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Сервис для управления продуктами.
 * Отвечает за CRUD-операции с продуктами и кэширование.
 */
@Service
@Primary
@Qualifier("standard")
@Slf4j
public class ProductService implements IProductService {

    private final IAuthorizationService authorizationService;

    @Autowired
    private ProductRepository productRepository;

    ProductService(IAuthorizationService authorizationService){
        this.authorizationService = authorizationService;
    }

    /**
     * Получает продукты с пагинацией.
     * Аннотация @Cacheable используется для кэширования результата метода, чтобы при следующем вызове
     * с теми же параметрами результат возвращался из кэша.
     *
     * @param page Номер страницы.
     * @param size Количество элементов на странице.
     * @param sort Критерии сортировки (property and direction).
     * @throws InvalidArgumentsToPaginationException Если page или size отрицательные.
     * @throws InvalidSortDirectionException Если direction сортировки неверное (не "asc" или "desc").
     * @throws ProductsEmptyException Если в базе данных нет продуктов.
     * @return Список продуктов с пагинацией.
     */
    @Cacheable(value = "products", key = "#page.toString() + #size.toString() + T(java.util.Arrays).toString(#sort)")
    public Iterable<ProductDTO> getWithPagination(int page, int size, String[] sort) {

        if (page < 0 || size < 0) {
            throw new InvalidArgumentsToPaginationException();
        }

        if (sort.length != 2 || (!sort[1].equalsIgnoreCase("asc") && !sort[1].equalsIgnoreCase("desc"))) {
            throw new InvalidSortDirectionException();
        }

        // Если размер больше 60, устанавливаем его равным 60
        if (size > 60) {
            size = 60;
        }

        log.info("Getting all products with pagination, page {} and size {}", page, size);

        String property = sort[0];
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);

        Pageable pageRequest = PageRequest.of(page, size, Sort.by(direction, property));

        Page<Product> productPage = productRepository.findAll(pageRequest);

        if (!productPage.iterator().hasNext()) {
            throw new ProductsEmptyException();
        }

        return productPage.map(ProductDTO::from);
    }

    /**
     * Создаёт новый продукт с указанными данными и сохраняет его в базе данных.
     * Аннотация @CacheEvict используется для удаления всех записей из кэша при создании нового продукта.
     *
     * @param product Данные нового продукта.
     */
    @CacheEvict(value = "products", allEntries = true)
    public void createProduct(ProductDTO product) {
        log.info("Creating product with name {}", product.name());

        if (productRepository.existsByCode(product.code())){
            throw new ModelValidationException("product.code_already_exists", product.code());
        }

        if (productRepository.existsByName(product.name())){
            throw new ModelValidationException("product.name_already_exists", product.name());
        }

        long currentUserId = authorizationService.getCurrentUserId();

        Product newProduct = Product.builder()
                .code(product.code())
                .name(product.name())
                .price(product.price())
                .description(product.description())
                .build();
        newProduct.setEntryBy(currentUserId);
        newProduct.setEntryDate(LocalDateTime.now());

        productRepository.save(newProduct);
    }

    /**
     * Получает продукт по его ID.
     * Аннотация @Cacheable используется для кэширования результата метода, чтобы при следующем вызове
     * с теми же параметрами результат возвращался из кэша.
     *
     * @param productId ID продукта для получения.
     * @throws ProductNotFoundException Если продукт не существует.
     * @return Продукт с указанным ID.
     */
    @Cacheable(value = "products", key = "#productId")
    public Optional<ProductDTO> getProductById(Long productId) {
        log.info("Getting product by ID {}", productId);

        Optional<Product> product = productRepository.findById(productId);

        // Возвращаем продукт, если он существует, иначе выбрасываем исключение
        if (product.isPresent()) {
            return Optional.of(ProductDTO.from(product.get()));
        } else {
            throw new ProductNotFoundException(productId);
        }
    }

    /**
     * Обновляет продукт с указанным ID новыми данными.
     * Аннотация @CacheEvict используется для удаления всех записей из кэша при обновлении продукта.
     *
     * @param productId ID продукта для обновления.
     * @param updatedProduct Новые данные продукта.
     * @throws ProductNotFoundException Если продукт не существует.
     */
    @CacheEvict(value = "products", allEntries = true)
    public void updateProduct(Long productId, ProductDTO updatedProduct) {
        log.info("Updating product with ID {}", productId);

        Optional<Product> existingProduct = productRepository.findById(productId);
        long currentUserId = authorizationService.getCurrentUserId();

        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();

            product.setCode(updatedProduct.code());
            product.setName(updatedProduct.name());
            product.setPrice(updatedProduct.price());
            product.setDescription(updatedProduct.description());

            product.setUpdatedBy(currentUserId);
            product.setUpdatedDate(LocalDateTime.now());

            productRepository.save(product);
        } else {
            throw new ProductNotFoundException(productId);
        }
    }

    /**
     * Удаляет продукт с указанным ID.
     * Аннотация @CacheEvict используется для удаления всех записей из кэша при удалении продукта.
     *
     * @param productId ID продукта для удаления.
     * @throws ProductNotFoundException Если продукт не существует.
     */
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long productId) {
        log.info("Deleting product with ID {}", productId);

        Optional<Product> existingProduct = productRepository.findById(productId);

        if (existingProduct.isPresent()) {
            productRepository.delete(existingProduct.get());
        } else {
            throw new ProductNotFoundException(productId);
        }
    }

}
