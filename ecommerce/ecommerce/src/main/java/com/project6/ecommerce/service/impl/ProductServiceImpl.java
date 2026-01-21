package com.project6.ecommerce.service.impl;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.entity.Category;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.repository.CategoryRepository;
import com.project6.ecommerce.repository.ProductRepository;
import com.project6.ecommerce.service.ProductService;
import com.project6.ecommerce.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,StorageService storageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.storageService = storageService;
    }
    @Override
    public Product createProduct(CreateProductRequest request, MultipartFile imageFile){
        String savedFileName = null;
        Set<Category> categories = new HashSet<>();
        if (imageFile != null && !imageFile.isEmpty()) {
            savedFileName = storageService.store(imageFile);
        }
        if (request.categoryIds() != null && !request.categoryIds().isEmpty()) {
            categories = new HashSet<>(categoryRepository.findAllById(request.categoryIds()));
        }

        if (request.categoryIds() != null && categories.size() < request.categoryIds().size()) {
            throw new RuntimeException("Niektóre kategorie nie istnieją!");
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

}
