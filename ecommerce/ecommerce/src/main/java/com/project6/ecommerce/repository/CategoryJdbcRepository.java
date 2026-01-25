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

    private static final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        return category;
    };

    public List<Category> findAll() {
        String sql = "SELECT id, name FROM Category";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    public Category findById(Long id) {
        String sql = "SELECT id, name FROM Category WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, categoryRowMapper, id);
    }

    public int save(Category category) {
        String sql = "INSERT INTO Category (name) VALUES (?)";
        return jdbcTemplate.update(sql, category.getName());
    }

    public int update(Category category) {
        String sql = "UPDATE Category SET name = ? WHERE id = ?";
        return jdbcTemplate.update(sql, category.getName(), category.getId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM Category WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}