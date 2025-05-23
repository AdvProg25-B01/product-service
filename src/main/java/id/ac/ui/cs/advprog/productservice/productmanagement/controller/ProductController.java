package id.ac.ui.cs.advprog.productservice.productmanagement.controller;

import id.ac.ui.cs.advprog.productservice.productmanagement.factory.ProductFactory;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Create a new product (REST API version)
    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<?>> createProduct(@RequestBody Product incomingProduct) {
        try {
            Product product = ProductFactory.createProduct(
                    incomingProduct.getName(),
                    incomingProduct.getCategory(),
                    incomingProduct.getStock(),
                    incomingProduct.getPrice()
            );
            // Call the async service method
            return productService.addProduct(product, true)
                    .thenApply(success -> {
                        if (!success) {
                            return ResponseEntity.badRequest().body("Product is invalid or could not be created");
                        }
                        return ResponseEntity.status(HttpStatus.CREATED).body(product);
                    })
                    .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("An error occurred during product creation: " + ex.getMessage()));
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Product is invalid or could not be created"));
        }
    }

    // Get all products
    @GetMapping("/list")
    public ResponseEntity<List<Product>> listProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Get a single product
    @GetMapping("/{name}")
    public ResponseEntity<?> getProduct(@PathVariable String name) {
        List<Product> products = productService.getAllProducts();
        Product product = products.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }

    // Get a product by ID
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            List<Product> products = productService.getAllProducts();
            Product product = products.stream()
                    .filter(p -> p.getId().equals(uuid))
                    .findFirst()
                    .orElse(null);

            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product with ID '" + id + "' not found");
            }

            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID format: " + id);
        }
    }

    // Update an existing product
    @PutMapping("/edit/{name}")
    public CompletableFuture<ResponseEntity<?>> editProduct(@PathVariable String name,
                                                            @RequestBody Product product) {
        // Assume 'product' has the ID set for lookup in the service layer
        // If not, you might need to fetch the existing product by name first to get its ID,
        // and then pass the ID to the service method.
        // For simplicity, assuming product.getId() is set correctly here.

        return productService.editProduct(product, true) // Call the async service method
                .thenApply(success -> {
                    if (!success) {
                        return ResponseEntity.badRequest()
                                .body("Edit failed. Product could not be updated or not found.");
                    }
                    return ResponseEntity.ok(product);
                })
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred during product edit: " + ex.getMessage()));
    }

    // Delete a product
    @DeleteMapping("/delete/{name}")
    public CompletableFuture<ResponseEntity<String>> deleteProduct(@PathVariable String name) {
        return productService.deleteProduct(name, true) // Call the async service method
                .thenApply(success -> {
                    if (!success) {
                        return ResponseEntity.badRequest()
                                .body("Product deletion failed or product not found.");
                    }
                    return ResponseEntity.ok().body("Product deleted successfully");
                })
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred during product deletion: " + ex.getMessage()));
    }
}