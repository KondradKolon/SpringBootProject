package com.project6.ecommerce.service.impl;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.repository.ProductRepository;
import com.project6.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public Product createProduct(CreateProductRequest request){
        Product product = new Product(
                null,
                request.name(),
                request.description(),
                request.price(),
                request.quantity(),
                request.image_url(),
                request.status()
        );
        return productRepository.save(product);
    }

}
