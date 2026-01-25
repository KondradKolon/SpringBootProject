package com.project6.ecommerce.repository;

import com.project6.ecommerce.domain.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryJdbcRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CategoryJdbcRepository repository;

    @Test
    void findAll_ShouldReturnList() {
        // Arrange
        Category category = new Category(1L, "Test");
        when(jdbcTemplate.query(any(String.class), any(RowMapper.class))).thenReturn(List.of(category));

        // Act
        List<Category> result = repository.findAll();

        // Assert
        assertEquals(1, result.size());
        verify(jdbcTemplate).query(eq("SELECT id, name FROM Category"), any(RowMapper.class));
    }

    @Test
    void findById_ShouldReturnCategory() {
        // Arrange
        Category category = new Category(1L, "Test");
        when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class), eq(1L))).thenReturn(category);

        // Act
        Category result = repository.findById(1L);

        // Assert
        assertEquals("Test", result.getName());
    }

    @Test
    void save_ShouldReturnInt() {
        // Arrange
        Category category = new Category(null, "New");
        when(jdbcTemplate.update(any(String.class), eq("New"))).thenReturn(1);

        // Act
        int result = repository.save(category);

        // Assert
        assertEquals(1, result);
        verify(jdbcTemplate).update(eq("INSERT INTO Category (name) VALUES (?)"), eq("New"));
    }

    @Test
    void update_ShouldReturnInt() {
        // Arrange
        Category category = new Category(1L, "Updated");
        when(jdbcTemplate.update(any(String.class), eq("Updated"), eq(1L))).thenReturn(1);

        // Act
        int result = repository.update(category);

        // Assert
        assertEquals(1, result);
        verify(jdbcTemplate).update(eq("UPDATE Category SET name = ? WHERE id = ?"), eq("Updated"), eq(1L));
    }

    @Test
    void deleteById_ShouldReturnInt() {
        // Arrange
        when(jdbcTemplate.update(any(String.class), eq(1L))).thenReturn(1);

        // Act
        int result = repository.deleteById(1L);

        // Assert
        assertEquals(1, result);
        verify(jdbcTemplate).update(eq("DELETE FROM Category WHERE id = ?"), eq(1L));
    }
}
