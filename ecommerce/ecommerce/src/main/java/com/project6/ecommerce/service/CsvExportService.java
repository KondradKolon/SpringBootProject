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
            // nagłówek kolumn
            writer.write("ID,Name,Price,Quantity,Status\n");

            // zapis rekordu po rekordzie
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

    // zabezpieczenie przed zepsuciem formatu csv gdy w danych są przecinki lub cudzysłowy
    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\\R", " "); // usuń znaki nowej linii
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\""); // podwójny cudzysłów (escaping)
            escapedData = "\"" + data + "\""; // całość w cudzysłów
        }
        return escapedData;
    }
}
