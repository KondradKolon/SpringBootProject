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
    private final com.project6.ecommerce.repository.UserRepository userRepository; // Direct repo access for simple check
    
    @Override
    public void run(String... args) throws Exception {
        // 1. Inicjalizacja Admina
        try {
            CreateUserRequest adminRequest = new CreateUserRequest("admin123", "admin@example.com", "123", role.ADMIN);
            userService.createUser(adminRequest);
            System.out.println("✅ Admin initialized: admin123 / 123");
        } catch (RuntimeException e) {
            // Ignorujemy, jeśli już istnieje (na podstawie logiki serwisu)
             System.out.println("ℹ️ Admin already exists or error: " + e.getMessage());
        }
        
        // 2. Inicjalizacja userów
        if (!userRepository.existsByEmail("user@example.com")) {
            CreateUserRequest userRequest = new CreateUserRequest("user1", "user@example.com", "123", role.USER);
            userService.createUser(userRequest);
        }
        if (!userRepository.existsByEmail("annakowalska@example.com")) {
            CreateUserRequest userRequest2 = new CreateUserRequest("anna", "annakowalska@example.com", "123", role.USER);
            userService.createUser(userRequest2);
        }
        if (!userRepository.existsByEmail("janusz@biznes.pl")) {
            CreateUserRequest userRequest3 = new CreateUserRequest("janusz", "janusz@biznes.pl", "123", role.USER);
            userService.createUser(userRequest3);
        }

        // 3. Inicjalizacja Kategorii
        if (categoryService.getAllCategories().isEmpty()) {
            categoryService.createCategory(new Category(null, "Electronics"));
            categoryService.createCategory(new Category(null, "Books"));
            categoryService.createCategory(new Category(null, "Clothing"));
            categoryService.createCategory(new Category(null, "Home & Garden"));
            categoryService.createCategory(new Category(null, "Sports"));
            categoryService.createCategory(new Category(null, "Automotive"));
            System.out.println("✅ Categories initialized");
        }

        // 3. Inicjalizacja Produktów (Bogaty zestaw danych)
        if (productService.getAllProducts(org.springframework.data.domain.Pageable.unpaged()).isEmpty()) {
             // 1. Elektronika
             createProduct("Smartphone X", "Flagowy model 2025 z AI", new BigDecimal("3999.99"), 50, status.AVAILABLE, "Electronics");
             createProduct("Laptop Pro 15", "M3 Max, 32GB RAM, 1TB SSD", new BigDecimal("8499.00"), 20, status.AVAILABLE, "Electronics");
             createProduct("Słuchawki NoiseCancel", "Idealna cisza w podróży", new BigDecimal("1299.00"), 100, status.AVAILABLE, "Electronics");
             createProduct("Smartwatch Ultra", "Bateria 7 dni, tytanowa koperta", new BigDecimal("1899.00"), 0, status.NOT_AVAILABLE, "Electronics"); // NIEDOSTĘPNY
             createProduct("Monitor 4K 27\"", "IPS, 144Hz, HDR", new BigDecimal("2299.00"), 15, status.AVAILABLE, "Electronics");

             // 2. Książki
             createProduct("Java Programming", "Kompletny przewodnik dla opornych", new BigDecimal("89.00"), 100, status.AVAILABLE, "Books");
             createProduct("Mroczny Sekret", "Najlepszy thriller roku", new BigDecimal("45.00"), 200, status.AVAILABLE, "Books");
             createProduct("Kuchnia Włoska", "Przepisy babci", new BigDecimal("59.00"), 50, status.AVAILABLE, "Books");

             // 3. Dom i Ogród
             createProduct("Krzesło Gamingowe", "Ergonomiczne, RGB", new BigDecimal("899.00"), 30, status.AVAILABLE, "Home & Garden");
             createProduct("Lampa Biurkowa", "LED, regulacja barwy", new BigDecimal("129.00"), 150, status.AVAILABLE, "Home & Garden");
             
             // 4. Sport
             createProduct("Rower Górski MTB", "Aluminium, amortyzatory", new BigDecimal("2499.00"), 10, status.AVAILABLE, "Sports");
             createProduct("Mata do Jogi", "Antypoślizgowa gruba", new BigDecimal("79.00"), 300, status.AVAILABLE, "Sports");

             // 5. Motoryzacja
             createProduct("Wideorejestrator 4K", "Kamera samochodowa przód/tył", new BigDecimal("599.00"), 40, status.AVAILABLE, "Automotive");
             createProduct("Zestaw Kosmetyków Auto", "Szampon, Wosk, Gąbka", new BigDecimal("149.00"), 100, status.AVAILABLE, "Automotive");

             System.out.println("✅ Products initialized (Rich Dataset)");
        }
    }

    private void createProduct(String name, String desc, BigDecimal price, int qty, status status, String categoryName) {
        // Znajdź kategorię po nazwie (żeby powiązać)
        Category cat = categoryService.getAllCategories().stream()
                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElse(null);

        java.util.Set<Long> catIds = cat != null ? java.util.Collections.singleton(cat.getId()) : null;

        CreateProductRequest req = new CreateProductRequest(
                name, desc, price, qty, catIds, null, status
        );
        productService.createProduct(req, null); // Bez zdjęcia na start
    }
}
