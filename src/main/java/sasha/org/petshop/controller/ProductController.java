package sasha.org.petshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sasha.org.petshop.dto.ProductDTO;
import sasha.org.petshop.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/createProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> createProduct(@RequestBody ProductDTO productDTO) {
        boolean created = productService.createProduct(productDTO);
        return created
                ? ResponseEntity.status(HttpStatus.CREATED).body(true)  // Created successfully
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false); // Failed to create
    }

    @PostMapping("/admin/updateProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> updateProduct(@RequestBody ProductDTO productDTO) {
        boolean updated = productService.updateProduct(productDTO);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).body(true)  // Updated successfully
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Product not found to update
    }

    @GetMapping("/ListOfAllProducts")
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return products; // Return list of products
    }

    @DeleteMapping("/admin/deleteProduct/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable("id") Integer id) {
        boolean deleted = productService.deleteProductById(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(true)  // Deleted successfully
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Product not found to delete
    }
}
