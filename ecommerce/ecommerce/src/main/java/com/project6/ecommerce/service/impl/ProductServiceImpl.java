package com.project6.ecommerce.service.impl;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.entity.Category;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.repository.CategoryRepository;
import com.project6.ecommerce.repository.ProductRepository;
import com.project6.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Product createProduct(CreateProductRequest request){
        Set<Category> categories = new HashSet<>();
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
                request.image_url(),
                request.status()
        );
        return productRepository.save(product);
    }

}
