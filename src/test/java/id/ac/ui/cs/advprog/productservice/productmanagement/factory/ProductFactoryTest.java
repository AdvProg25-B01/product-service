package id.ac.ui.cs.advprog.productservice.productmanagement.factory;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductFactoryTest {

    @Test
    void testCreateValidProduct() {
        Product product = ProductFactory.createProduct("Laptop", "Elektronik", 30, 15000000.0);

        assertNotNull(product);
        assertEquals("Laptop", product.getName());
        assertEquals("Elektronik", product.getCategory());
        assertEquals(30, product.getStock());
        assertEquals(15000000.0, product.getPrice());
    }

    @Test
    void testCreateInvalidProduct() {
        assertThrows(IllegalArgumentException.class, () -> {
            ProductFactory.createProduct("", "Elektronik", 30, 15000000.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ProductFactory.createProduct("Laptop", "", 30, 15000000.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ProductFactory.createProduct("Laptop", "Elektronik", 0, 15000000.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ProductFactory.createProduct("Laptop", "Elektronik", 30, -5000.0);
        });
    }
}