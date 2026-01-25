package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.entity.Category;
import com.project6.ecommerce.repository.CategoryJdbcRepository;
import com.project6.ecommerce.repository.CategoryRepository;
import com.project6.ecommerce.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryJdbcRepository categoryJdbcRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Electronics");
    }

    @Test
    void getAllCategories_ShouldReturnList_FromJdbc() {
        // Arrange
        when(categoryJdbcRepository.findAll()).thenReturn(List.of(category));

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryJdbcRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenExists() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(1L);
        });
        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void createCategory_ShouldSave() {
        // Act
        categoryService.createCategory(category);

        // Assert
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCategory_ShouldSave() {
        // Act
        categoryService.updateCategory(category);

        // Assert
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void deleteCategory_ShouldDelete() {
        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
