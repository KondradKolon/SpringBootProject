package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.entity.Category;
import com.project6.ecommerce.service.CategoryService;
import com.project6.ecommerce.service.ProductService; // Added to fix context failure? No, unnecessary if slice is correct.
import com.project6.ecommerce.service.UserService;
import com.project6.ecommerce.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void listCategories_ShouldReturnViewAndModel() throws Exception {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(List.of(new Category(1L, "Test")));

        // Act & Assert
        mockMvc.perform(get("/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categories-list"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void showAddForm_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/admin/categories/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category-form"))
                .andExpect(model().attributeExists("category"));
    }

    @Test
    void addCategory_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/admin/categories/add")
                        .flashAttr("category", new Category(null, "New")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));

        verify(categoryService).createCategory(any(Category.class));
    }

    @Test
    void showEditForm_ShouldReturnView_WhenExists() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(new Category(1L, "Edit"));

        mockMvc.perform(get("/admin/categories/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category-form"))
                .andExpect(model().attribute("category", new Category(1L, "Edit")));
    }

    @Test
    void updateCategory_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/admin/categories/edit")
                        .flashAttr("category", new Category(1L, "Updated")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));

        verify(categoryService).updateCategory(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/admin/categories/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));

        verify(categoryService).deleteCategory(1L);
    }
}
