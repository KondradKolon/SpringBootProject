package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Service
public class CsvExportService {

    public void writeProductsToCsv(Writer writer, List<ProductDto> products) {
        try {
                
            writer.write("ID,Name,Price,Quantity,Status\n");

            for (ProductDto product : products) {
                writer.write(escapeSpecialCharacters(product.id().toString()) + ",");
                writer.write(escapeSpecialCharacters(product.name()) + ",");
                writer.write(product.price() + ",");
                writer.write(product.quantity() + ",");
                writer.write(product.status() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("błąd generowania pliku csv", e);
        }
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\\R", " "); 
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\""); 
            escapedData = "\"" + data + "\""; 
        }
        return escapedData;
    }
}
