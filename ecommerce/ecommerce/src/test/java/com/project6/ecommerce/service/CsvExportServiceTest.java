package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.status;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvExportServiceTest {

    private final CsvExportService service = new CsvExportService();

    @Test
    void writeProductsToCsv_ShouldGenerateCorrectFormat() {
        // Arrange
        ProductDto p1 = new ProductDto(UUID.randomUUID(), "Product 1", "Desc", BigDecimal.TEN, 5, null, null, status.AVAILABLE, 0.0, 0);
        ProductDto p2 = new ProductDto(UUID.randomUUID(), "Product 2, Special", "Desc", BigDecimal.valueOf(20.50), 2, null, null, status.NOT_AVAILABLE, 0.0, 0);
        List<ProductDto> products = List.of(p1, p2);
        StringWriter writer = new StringWriter();

        // Act
        service.writeProductsToCsv(writer, products);
        String result = writer.toString();

        // Assert
        assertTrue(result.contains("ID,Name,Price,Quantity,Status")); // Header
        assertTrue(result.contains("Product 1,10,5,AVAILABLE")); // Row 1
        assertTrue(result.contains("\"Product 2, Special\",20.5,2,NOT_AVAILABLE")); // Row 2 (Escaped comma)
    }
}
