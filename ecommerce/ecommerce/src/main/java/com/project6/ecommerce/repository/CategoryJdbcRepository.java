package com.project6.ecommerce.repository;

import com.project6.ecommerce.domain.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    // ▼▼▼ WYMÓG 1: RowMapper (Mapowanie wiersza z bazy na obiekt Java) ▼▼▼

    // ▼▼▼ WYMÓG 2: Zapytania SELECT z query() ▼▼▼
    public List<Category> findAll() {
        // Zakładam, że tabela nazywa się 'Category' (lub 'category' - w H2 zazwyczaj bez znaczenia)
        String sql = "SELECT id, name FROM Category";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    public Category findById(Long id) {
        String sql = "SELECT id, name FROM Category WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, categoryRowMapper, id);
    }

    // ▼▼▼ WYMÓG 3: Operacje INSERT z update() ▼▼▼
    public int save(Category category) {
        // WAŻNE: Nie podajemy ID, bo baza generuje je sama (IDENTITY)
        String sql = "INSERT INTO Category (name) VALUES (?)";
        return jdbcTemplate.update(sql, category.getName());
    }

    // ▼▼▼ WYMÓG 4: Operacje UPDATE z update() ▼▼▼
    public int update(Category category) {
        String sql = "UPDATE Category SET name = ? WHERE id = ?";
        return jdbcTemplate.update(sql, category.getName(), category.getId());
    }

    // ▼▼▼ WYMÓG 5: Operacje DELETE z update() ▼▼▼
    public int deleteById(Long id) {
        String sql = "DELETE FROM Category WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}