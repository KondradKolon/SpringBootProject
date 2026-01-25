package com.project6.ecommerce.service.impl;

import com.project6.ecommerce.domain.entity.Category;
import com.project6.ecommerce.repository.CategoryRepository;
import com.project6.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final com.project6.ecommerce.repository.CategoryJdbcRepository categoryJdbcRepository; // Inject JDBC Repo

    @Override
    public List<Category> getAllCategories() {
        return categoryJdbcRepository.findAll(); // Use JDBC for reading list
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void updateCategory(Category category) {
        // JPA save does update if ID exists
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
