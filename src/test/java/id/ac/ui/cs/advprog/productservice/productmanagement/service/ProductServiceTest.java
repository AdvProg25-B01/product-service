package id.ac.ui.cs.advprog.productservice.productmanagement.service;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService(new ProductRepository());
    }

    @Test
    public void testAddValidProductShouldBeSaved() {
        Product product = new Product("Laptop", "Elektronik", 30,15000000.0);
        boolean result = productService.addProduct(product, true); // true = user konfirmasi

        assertTrue(result);
        assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    public void testAddInvalidProductShouldNotBeSaved() {
        Product product = new Product("Laptop", "Elektronik", 30, -5000.0);
        boolean result = productService.addProduct(product, true);

        assertFalse(result);
        assertEquals(0, productService.getAllProducts().size());
    }

    @Test
    public void testAddProductWithoutConfirmationShouldNotBeSaved() {
        Product product = new Product("Laptop", "Elektronik", 30,15000000.0);
        boolean result = productService.addProduct(product, false); // user tidak konfirmasi

        assertFalse(result);
        assertEquals(0, productService.getAllProducts().size());
    }

    @Test
    public void testEditProductSuccessfully() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        productService.addProduct(product, true);

        Product updated = new Product("Laptop", "Gadget", 20, 12000000.0);
        boolean result = productService.editProduct(updated, true); // konfirmasi

        assertTrue(result);
        Product found = productService.getAllProducts().get(0);
        assertEquals("Gadget", found.getCategory());
        assertEquals(12000000.0, found.getPrice());
    }

    @Test
    public void testEditProductWithoutConfirmationShouldNotChange() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        productService.addProduct(product, true);

        Product updated = new Product("Laptop", "Gadget", 30, 12000000.0);
        boolean result = productService.editProduct(updated, false); // batal konfirmasi

        assertFalse(result);
        Product found = productService.getAllProducts().get(0);
        assertEquals("Elektronik", found.getCategory());
    }

    @Test
    public void testDeleteProductSuccessfully() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        productService.addProduct(product, true);

        boolean result = productService.deleteProduct("Laptop", true);
        assertTrue(result);
        assertTrue(productService.getAllProducts().isEmpty());
    }

    @Test
    public void testDeleteProductWithoutConfirmationShouldNotDelete() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        productService.addProduct(product, true);

        boolean result = productService.deleteProduct("Laptop", false);
        assertFalse(result);
        assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    public void testGetProductByIdSuccess() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        productService.addProduct(product, true);

        String id = product.getId();
        Product found = productService.getProductById(id).orElse(null);

        assertNotNull(found);
        assertEquals(id, found.getId());
        assertEquals("Laptop", found.getName());
    }

    @Test
    public void testGetProductByIdNotFound() {
        Product found = productService.getProductById("non-existent-id").orElse(null);
        assertNull(found);
    }
}