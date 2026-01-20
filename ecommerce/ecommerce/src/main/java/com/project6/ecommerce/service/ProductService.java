package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.entity.Product.Product;

public interface ProductService {
    Product createProduct(CreateProductRequest request);
}
