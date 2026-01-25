package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.CreateProductRequestDto;
import com.project6.ecommerce.mapper.ProductMapper;
import com.project6.ecommerce.service.CsvExportService;
import com.project6.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final com.project6.ecommerce.service.CategoryService categoryService; 
    private final ProductMapper productMapper;

    private final CsvExportService csvExportService; 

    // 1. GET: Wyświetl pusty formularz
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        // Przekazujemy pusty obiekt DTO, do którego formularz "przyklei" dane
        model.addAttribute("productDto", new CreateProductRequestDto(null, null, null, null, null, null,null));

        // Przekazujemy listę kategorii, żeby wyświetlić je w <select>
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/product-form"; // Szuka: templates/admin/product-form.html
    }

    // 2. POST: Odbierz dane i zapisz
    @PostMapping("/add")
    public String addProduct(
            @Valid @ModelAttribute("productDto") CreateProductRequestDto productDto, // Dane tekstowe
            BindingResult bindingResult, // Wynik walidacji (błędy)
            @RequestParam("image") MultipartFile imageFile, // Plik
            Model model,
            RedirectAttributes redirectAttributes // Do wyświetlenia komunikatu "Sukces" po przekierowaniu
    ) {
        // A. Jeśli są błędy walidacji (np. pusta cena), wróć do formularza
        if (bindingResult.hasErrors()) {
            // Musimy ponownie załadować kategorie, bo strona się odświeża
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/product-form";
        }

        // B. Logika biznesowa (używamy tego samego serwisu co w REST API!)
        try {
            CreateProductRequest request = productMapper.fromDto(productDto);
            productService.createProduct(request, imageFile);

            redirectAttributes.addFlashAttribute("message", "Produkt został dodany pomyślnie!");
        } catch (Exception e) {
            // Obsługa błędu (np. nie udało się zapisać pliku)
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("error", "Błąd zapisu: " + e.getMessage());
            return "admin/product-form";
        }

        // C. Pattern PRG (Post-Redirect-Get) - żeby odświeżenie strony nie dodało produktu 2 razy
        return "redirect:/admin/products/add";
    }

    // 3. Export to CSV
    @GetMapping("/export/csv")
    public void downloadCsv(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"products.csv\"");

        // Get all products (unpaged)
        var products = productService.getAllProducts(org.springframework.data.domain.Pageable.unpaged()).getContent();
        
        csvExportService.writeProductsToCsv(response.getWriter(), products);
    }
}