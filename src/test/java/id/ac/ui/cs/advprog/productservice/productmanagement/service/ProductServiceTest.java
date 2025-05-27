package id.ac.ui.cs.advprog.productservice.productmanagement.service;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks; // New import
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture; // New import
import java.util.concurrent.ExecutionException; // New import

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks // Inject mocks into this instance
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // No manual instantiation needed here, @InjectMocks handles it
    }

    @Test
    void testAddProductSuccess() throws ExecutionException, InterruptedException { // Added throws
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        when(productRepository.save(any(Product.class))).thenReturn(product); // Mock the save operation if needed

        CompletableFuture<Boolean> futureResult = productService.addProduct(product, true);

        assertTrue(futureResult.get()); // Get the result from CompletableFuture
        verify(productRepository).save(product);
    }

    @Test
    void testAddProductNotConfirmed() throws ExecutionException, InterruptedException { // Added throws
        Product product = new Product("Laptop", "Electronics", 10, 999.99);

        CompletableFuture<Boolean> futureResult = productService.addProduct(product, false);

        assertFalse(futureResult.get()); // Get the result from CompletableFuture
        verify(productRepository, never()).save(any());
    }

    @Test
    void testAddProductInvalidPrice() throws ExecutionException, InterruptedException { // Added throws
        Product product = new Product("Laptop", "Electronics", 10, -100.0);

        CompletableFuture<Boolean> futureResult = productService.addProduct(product, true);

        assertFalse(futureResult.get()); // Get the result from CompletableFuture
        verify(productRepository, never()).save(any());
    }

    @Test
    void testEditProductSuccess() throws ExecutionException, InterruptedException { // Added throws
        UUID productId = UUID.randomUUID();
        Product existingProduct = new Product("Laptop", "Electronics", 10, 999.99);
        existingProduct.setId(productId);

        Product updatedProduct = new Product("Laptop", "Computing", 15, 1199.99);
        updatedProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct); // Mock the save

        CompletableFuture<Boolean> futureResult = productService.editProduct(updatedProduct, true);

        assertTrue(futureResult.get()); // Get the result from CompletableFuture
        verify(productRepository).findById(productId);
        verify(productRepository).save(existingProduct);
        assertEquals("Computing", existingProduct.getCategory());
        assertEquals(15, existingProduct.getStock());
        assertEquals(1199.99, existingProduct.getPrice());
    }

    @Test
    void testEditProductNotConfirmed() throws ExecutionException, InterruptedException { // Added throws
        Product updatedProduct = new Product("Laptop", "Computing", 15, 1199.99);

        CompletableFuture<Boolean> futureResult = productService.editProduct(updatedProduct, false);

        assertFalse(futureResult.get()); // Get the result from CompletableFuture
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testEditProductNotFound() throws ExecutionException, InterruptedException { // Added throws
        UUID productId = UUID.randomUUID();
        Product updatedProduct = new Product("Laptop", "Computing", 15, 1199.99);
        updatedProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        CompletableFuture<Boolean> futureResult = productService.editProduct(updatedProduct, true);

        assertFalse(futureResult.get()); // Get the result from CompletableFuture
        verify(productRepository).findById(productId); // Verify findById was called
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProductSuccess() throws ExecutionException, InterruptedException { // Added throws
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        when(productRepository.findByName("Laptop")).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product); // Mock void method

        CompletableFuture<Boolean> futureResult = productService.deleteProduct("Laptop", true);

        assertTrue(futureResult.get()); // Get the result from CompletableFuture
        verify(productRepository).findByName("Laptop");
        verify(productRepository).delete(product);
    }

    @Test
    void testDeleteProductNotConfirmed() throws ExecutionException, InterruptedException { // Added throws
        CompletableFuture<Boolean> futureResult = productService.deleteProduct("Laptop", false);

        assertFalse(futureResult.get()); // Get the result from CompletableFuture
        verify(productRepository, never()).findByName(any());
        verify(productRepository, never()).delete(any());
    }

    @Test
    void testDeleteProductNotFound() throws ExecutionException, InterruptedException { // Added throws
        when(productRepository.findByName("Laptop")).thenReturn(Optional.empty());

        CompletableFuture<Boolean> futureResult = productService.deleteProduct("Laptop", true);

        assertFalse(futureResult.get()); // Get the result from CompletableFuture
        verify(productRepository).findByName("Laptop"); // Verify findByName was called
        verify(productRepository, never()).delete(any());
    }

    // --- Synchronous methods tests remain unchanged ---
    @Test
    void testGetProductByIdSuccess() {
        UUID productId = UUID.randomUUID();
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(productId.toString());

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    void testGetProductByIdInvalidUUID() {
        Optional<Product> result = productService.getProductById("invalid-uuid");

        assertTrue(result.isEmpty());
        verify(productRepository, never()).findById(any()); // No call made for invalid UUID
    }

    @Test
    void testGetProductByIdNotFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(productId.toString());

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = Arrays.asList(
                new Product("Laptop", "Electronics", 10, 999.99),
                new Product("Mouse", "Electronics", 50, 29.99)
        );
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mouse", result.get(1).getName());
    }
}