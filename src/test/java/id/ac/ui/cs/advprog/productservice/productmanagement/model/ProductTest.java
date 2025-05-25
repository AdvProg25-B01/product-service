package id.ac.ui.cs.advprog.productservice.productmanagement.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    void testCreateProductWithValidData() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);

        assertEquals("Laptop", product.getName());
        assertEquals("Elektronik", product.getCategory());
        assertEquals(15000000.0, product.getPrice());
    }

    @Test
    void testSetCategory() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        product.setCategory("Gadget");

        assertEquals("Gadget", product.getCategory());
    }

    @Test
    void testSetStock() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        product.setStock(20);

        assertEquals(20, product.getStock());
    }

    @Test
    void testSetPrice() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        product.setPrice(12000000.0);

        assertEquals(12000000.0, product.getPrice());
    }
}
