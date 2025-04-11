package id.ac.ui.cs.advprog.productservice.productmanagement.integration;

import id.ac.ui.cs.advprog.productservice.productmanagement.controller.ProductController;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.repository.ProductRepository;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.factory.DefaultProductServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceIntegrationTest {

    @Autowired
    private ProductController productController;

    @Autowired
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new DefaultProductServiceFactory(productRepository).createProductService();
    }

    @Test
    void testAddProductIntegration() {
        Product product = new Product("Laptop", "Electronics", 15000000.0);

        boolean result = productService.addProduct(product, true);

        assertTrue(result);
        // Simulate checking the repository to see if the product was added successfully
        verify(productRepository).save(product);
    }
}