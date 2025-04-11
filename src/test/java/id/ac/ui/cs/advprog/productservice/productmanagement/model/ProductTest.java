package id.ac.ui.cs.advprog.productservice.productmanagement.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    public void testCreateProductWithValidData() {
        Product product = new Product("Laptop", "Elektronik", 15000000.0);

        assertEquals("Laptop", product.getName());
        assertEquals("Elektronik", product.getCategory());
        assertEquals(15000000.0, product.getPrice());
    }
}