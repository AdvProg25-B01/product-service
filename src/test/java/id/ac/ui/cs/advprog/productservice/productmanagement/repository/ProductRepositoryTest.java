package id.ac.ui.cs.advprog.productservice.productmanagement.repository;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRepositoryTest {

    private ProductRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new ProductRepository();
    }

    @Test
    public void testSaveProduct() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        repository.save(product);

        List<Product> products = repository.findAll();
        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }

    @Test
    public void testFindAllProducts() {
        repository.save(new Product("Laptop", "Elektronik", 30, 15000000.0));
        repository.save(new Product("Buku", "Edukasi", 10, 50000.0));

        List<Product> products = repository.findAll();
        assertEquals(2, products.size());
    }

    @Test
    public void testUpdateProduct() {
        Product original = new Product("Laptop", "Elektronik", 30, 15000000.0);
        repository.save(original);

        Product updated = new Product("Laptop", "Gadget", 20, 12000000.0);
        repository.update(updated);

        Product found = repository.findByName("Laptop").orElse(null);
        assertNotNull(found);
        assertEquals("Gadget", found.getCategory());
        assertEquals(20, found.getStock());
        assertEquals(12000000.0, found.getPrice());
    }

    @Test
    public void testDeleteProduct() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        repository.save(product);

        repository.delete("Laptop");

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testConfirmBeforeSaveOrDelete() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);

        // Simulasi: user batal konfirmasi → tidak menyimpan
        // Tidak save ke repo

        assertTrue(repository.findAll().isEmpty());

        // Simulasi: user konfirmasi → simpan
        repository.save(product);
        assertFalse(repository.findAll().isEmpty());

        // Simulasi: user batal hapus
        // Tidak hapus produk

        assertEquals(1, repository.findAll().size());

        // Simulasi: user konfirmasi hapus
        repository.delete("Laptop");
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    public void testFindByIdSuccess() {
        Product product = new Product("Laptop", "Elektronik", 30, 15000000.0);
        repository.save(product);

        String id = product.getId();
        Product found = repository.findById(id);

        assertNotNull(found);
        assertEquals(id, found.getId());
        assertEquals("Laptop", found.getName());
    }

    @Test
    public void testFindByIdNotFound() {
        Product found = repository.findById("non-existent-id");
        assertNull(found);
    }
}