package id.ac.ui.cs.advprog.productservice.productmanagement.repository;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository repository;

    @Test
    public void testSaveAndFindProduct() {
        // Create and save a product
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        Product saved = repository.save(product);

        assertNotNull(saved.getId());
        assertEquals("Laptop", saved.getName());
        assertEquals("Electronics", saved.getCategory());
        assertEquals(10, saved.getStock());
        assertEquals(999.99, saved.getPrice());
    }

    @Test
    public void testUpdateProduct() {
        // Create and save a product
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        Product saved = repository.save(product);

        // Update the product
        saved.setCategory("Computing");
        saved.setStock(15);
        saved.setPrice(1199.99);
        Product updated = repository.save(saved); // save() handles both create and update

        assertEquals("Computing", updated.getCategory());
        assertEquals(15, updated.getStock());
        assertEquals(1199.99, updated.getPrice());
    }

    @Test
    public void testDeleteProduct() {
        // Create and save a product
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        Product saved = repository.save(product);
        UUID productId = saved.getId();

        // Delete the product
        repository.delete(saved);

        // Verify it's deleted
        Optional<Product> found = repository.findById(productId);
        assertTrue(found.isEmpty());
    }

    @Test
    public void testDeleteProductByName() {
        // Create and save a product
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        repository.save(product);

        // Find and delete by name
        Optional<Product> productToDelete = repository.findByName("Laptop");
        assertTrue(productToDelete.isPresent());
        repository.delete(productToDelete.get());

        // Verify it's deleted
        Optional<Product> found = repository.findByName("Laptop");
        assertTrue(found.isEmpty());
    }

    @Test
    public void testFindById() {
        // Create and save a product
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        Product saved = repository.save(product);
        UUID productId = saved.getId();

        // Find by ID
        Optional<Product> found = repository.findById(productId);
        assertTrue(found.isPresent());
        assertEquals("Laptop", found.get().getName());
    }

    @Test
    public void testFindByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Product> found = repository.findById(nonExistentId);
        assertTrue(found.isEmpty());
    }

    @Test
    public void testFindByName() {
        // Create and save a product
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        repository.save(product);

        // Find by name
        Optional<Product> found = repository.findByName("Laptop");
        assertTrue(found.isPresent());
        assertEquals("Electronics", found.get().getCategory());
    }

    @Test
    public void testFindByNameNotFound() {
        Optional<Product> found = repository.findByName("NonExistentProduct");
        assertTrue(found.isEmpty());
    }

    @Test
    public void testFindAll() {
        // Create and save multiple products
        Product product1 = new Product("Laptop", "Electronics", 10, 999.99);
        Product product2 = new Product("Mouse", "Electronics", 50, 29.99);
        repository.save(product1);
        repository.save(product2);

        // Find all
        List<Product> products = repository.findAll();
        assertTrue(products.size() >= 2);
    }
}