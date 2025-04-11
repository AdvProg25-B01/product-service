package id.ac.ui.cs.advprog.productservice.productmanagement.service;

import id.ac.ui.cs.advprog.productservice.productmanagement.factory.ProductFactory;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.repository.ProductRepository;

import java.util.List;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Add Product with confirmation (using ProductFactory)
    public boolean addProduct(String name, String category, double price, boolean confirm) {
        if (!confirm) {
            return false; // If the user does not confirm, do not save the product
        }

        try {
            // Use the factory to create the product
            Product product = ProductFactory.createProduct(name, category, price);
            productRepository.save(product);  // Save product to repository
            return true;
        } catch (IllegalArgumentException e) {
            return false;  // If product creation fails due to invalid data, return false
        }
    }

    // Edit Product with confirmation (using ProductFactory)
    public boolean editProduct(Product updatedProduct, boolean confirm) {
        if (!confirm) {
            return false; // If the user does not confirm, do not save changes
        }

        try {
            Product existingProduct = productRepository.findByName(updatedProduct.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            // Use the factory to validate and update the product
            Product newProduct = ProductFactory.createProduct(updatedProduct.getName(),
                    updatedProduct.getCategory(), updatedProduct.getPrice());

            productRepository.update(newProduct);  // Update product in repository
            return true;
        } catch (IllegalArgumentException e) {
            return false;  // Return false if there's an issue with the update (e.g., product not found or invalid data)
        }
    }

    // Delete Product with confirmation
    public boolean deleteProduct(String name, boolean confirm) {
        if (!confirm) {
            return false; // If the user does not confirm, do not delete the product
        }

        Product product = productRepository.findByName(name).orElse(null);
        if (product != null) {
            productRepository.delete(name);  // Delete product from repository
            return true;
        }

        return false;  // If product is not found, return false
    }

    // Get all products from repository
    public List<Product> getAllProducts() {
        return productRepository.findAll();  // Retrieve all products from repository
    }
}