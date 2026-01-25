package com.project6.ecommerce.config;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.CreateUserRequest;
import com.project6.ecommerce.domain.entity.Category;
import com.project6.ecommerce.domain.entity.Product.status;
import com.project6.ecommerce.domain.entity.User.role;
import com.project6.ecommerce.service.CategoryService;
import com.project6.ecommerce.service.ProductService;
import com.project6.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        // 1. Inicjalizacja Admina
        // Using a try-catch pattern or a specific check method if added to service would be cleaner,
        // but since we modified UserService to only have create, we assume we might need to add validation there or here.
        // For now, let's assume we check via a new service method or just try to create and ignore "exists" exception.
        // To fix architecture, we MUST NOT use userRepository.
        
        // We'll trust the service handles "user exists" check or we add a helper.
        // Since we can't easily change the interface in one go without breaking others,
        // let's try to create and catch the exception we implemented in UserServiceImpl ("User with this email already exists")
        
        try {
            CreateUserRequest adminRequest = new CreateUserRequest("admin123", "admin@example.com", "123", role.ADMIN);
            userService.createUser(adminRequest);
            System.out.println("✅ Admin initialized: admin123 / 123");
        } catch (RuntimeException e) {
            // Ignorujemy, jeśli już istnieje (na podstawie logiki serwisu)
             System.out.println("ℹ️ Admin already exists or error: " + e.getMessage());
        }

        // 2. Inicjalizacja Kategorii
        if (categoryService.getAllCategories().isEmpty()) {
            categoryService.createCategory(new Category(null, "Electronics"));
            categoryService.createCategory(new Category(null, "Books"));
            categoryService.createCategory(new Category(null, "Clothing"));
            System.out.println("✅ Categories initialized");
        }

        // 3. Inicjalizacja Produktów
        // Note: getAllProducts usage depends on pagination, so we might check if empty page.
        if (productService.getAllProducts(org.springframework.data.domain.Pageable.unpaged()).isEmpty()) {
             CreateProductRequest p1 = new CreateProductRequest(
                 "Smartphone X", "Latest model smartphone", new BigDecimal("999.99"), 50, null, null, status.AVAILABLE
             );
             productService.createProduct(p1, null);

             CreateProductRequest p2 = new CreateProductRequest(
                 "Java Programming", "Best book for learning Java", new BigDecimal("49.99"), 100, null, null, status.AVAILABLE
             );
             productService.createProduct(p2, null);
             
            System.out.println("✅ Products initialized");
        }
    }
}
