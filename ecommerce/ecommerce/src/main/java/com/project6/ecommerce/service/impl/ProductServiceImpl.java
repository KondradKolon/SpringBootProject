package com.project6.ecommerce.service.impl;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Category;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.domain.entity.Review;
import com.project6.ecommerce.mapper.impl.ProductMapperImpl;
import com.project6.ecommerce.repository.CategoryRepository;
import com.project6.ecommerce.repository.ProductRepository;
import com.project6.ecommerce.repository.ReviewRepository;
import com.project6.ecommerce.service.ProductService;
import com.project6.ecommerce.storage.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import lombok.extern.slf4j.Slf4j; // Add import
import jakarta.persistence.criteria.Predicate;

@Service
@Slf4j // Add annotation
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    private final ProductMapperImpl productMapper;
    private final ReviewRepository reviewRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, StorageService storageService, ProductMapperImpl productMapper, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.storageService = storageService;
        this.productMapper = productMapper;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Product createProduct(CreateProductRequest request, MultipartFile imageFile) {
        log.info("Creating new product: {}", request.name()); // LOG

        String savedFileName = null;
        Set<Category> categories = new HashSet<>();
        if (imageFile != null && !imageFile.isEmpty()) {
            // zapisujemy plik na dysku i dostajemy nową nazwę
            savedFileName = storageService.store(imageFile);
        }
        if (request.categoryIds() != null && !request.categoryIds().isEmpty()) {
            categories = new HashSet<>(categoryRepository.findAllById(request.categoryIds()));
        }

        if (request.categoryIds() != null && categories.size() < request.categoryIds().size()) {
            log.error("Category validation failed for product: {}", request.name()); // LOG ERROR
            throw new RuntimeException("niektóre kategorie nie istnieją");
        }
        Product product = new Product(
                null,
                request.name(),
                request.description(),
                request.price(),
                request.quantity(),
                categories,
                savedFileName,
                request.status()
        );
        return productRepository.save(product);
    }

    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDto);
    }

    @Override
    public ProductDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produkt nie znaleziony"));
        List<Review> reviews = reviewRepository.findAllByProductIdOrderByCreatedAtDesc(id);

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return productMapper.toDtoWithRating(product, avg, reviews.size());
    }

    // aktualizacja danych produktu (bez zmiany zdjęcia dla uproszczenia)
    @Override
    @Transactional // transakcja wymagana żeby zapisać zmiany w bazie
    public ProductDto updateProduct(UUID id, CreateProductRequest request) {
        log.info("Updating product with ID: {}", id); // LOG

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("produkt nie znaleziony"));

        // aktualizacja prostych pól
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setStatus(request.status());

        // aktualizacja relacji z kategoriami tylko jeśli podano nowe
        if (request.categoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.categoryIds()));
            if (categories.size() < request.categoryIds().size()) {
                throw new RuntimeException("niektóre kategorie nie zalezione");
            }
            product.setCategories(categories);
        }

        Product savedProduct = productRepository.save(product);

        List<Review> reviews = reviewRepository.findAllByProductIdOrderByCreatedAtDesc(id);
        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // 5. Zwracamy kompletne DTO z ocenami
        return productMapper.toDtoWithRating(savedProduct, avg, reviews.size());
    }

    // ▼▼▼ NOWA METODA: DELETE ▼▼▼
    @Override
    public void deleteProduct(UUID id) {
        log.info("Deleting product with ID: {}", id); // LOG
        if (!productRepository.existsById(id)) {
            log.warn("Attempt to delete non-existent product ID: {}", id); // LOG WARN
            throw new RuntimeException("Produkt nie znaleziony");
        }
        productRepository.deleteById(id);
    }
    @Override
    public Page<ProductDto> getFilteredProducts(String search, BigDecimal minPrice, BigDecimal maxPrice, Long categoryId, String sort, Pageable pageable) {

        // dynamiczne budowanie zapytania sql (where ...) w zależności od filtrów
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // szukanie po fragmencie nazwy (case insensitive)
            if (search != null && !search.isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
            }

            // zakres cenowy od/do
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // filtrowanie po relacji many-to-many z kategoriami
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.join("categories").get("id"), categoryId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // obsługa sortowania wyników
        Sort sorting = Sort.unsorted();
        if (sort != null) {
            switch (sort) {
                case "price_asc" -> sorting = Sort.by("price").ascending();
                case "price_desc" -> sorting = Sort.by("price").descending();
                case "date_desc" -> sorting = Sort.by("id").descending();
                default -> sorting = Sort.unsorted();
            }
        }

        // łączenie sortowania z paginacją
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting);

        return productRepository.findAll(spec, sortedPageable)
                .map(productMapper::toDto);
    }


}