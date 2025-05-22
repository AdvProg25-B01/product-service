package id.ac.ui.cs.advprog.productservice.productmanagement.service;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    public void testAddProductSuccess() {
        Product product = new Product("Laptop", "Electronics", 10, 999.99);

        boolean result = productService.addProduct(product, true);

        assertTrue(result);
        verify(productRepository).save(product);
    }

    @Test
    public void testAddProductNotConfirmed() {
        Product product = new Product("Laptop", "Electronics", 10, 999.99);

        boolean result = productService.addProduct(product, false);

        assertFalse(result);
        verify(productRepository, never()).save(any());
    }

    @Test
    public void testAddProductInvalidPrice() {
        Product product = new Product("Laptop", "Electronics", 10, -100.0);

        boolean result = productService.addProduct(product, true);

        assertFalse(result);
        verify(productRepository, never()).save(any());
    }

    @Test
    public void testEditProductSuccess() {
        UUID productId = UUID.randomUUID();
        Product existingProduct = new Product("Laptop", "Electronics", 10, 999.99);
        existingProduct.setId(productId);

        Product updatedProduct = new Product("Laptop", "Computing", 15, 1199.99);
        updatedProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        boolean result = productService.editProduct(updatedProduct, true);

        assertTrue(result);
        verify(productRepository).save(existingProduct);
        assertEquals("Computing", existingProduct.getCategory());
        assertEquals(15, existingProduct.getStock());
        assertEquals(1199.99, existingProduct.getPrice());
    }

    @Test
    public void testEditProductNotConfirmed() {
        Product updatedProduct = new Product("Laptop", "Computing", 15, 1199.99);

        boolean result = productService.editProduct(updatedProduct, false);

        assertFalse(result);
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    public void testEditProductNotFound() {
        UUID productId = UUID.randomUUID();
        Product updatedProduct = new Product("Laptop", "Computing", 15, 1199.99);
        updatedProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        boolean result = productService.editProduct(updatedProduct, true);

        assertFalse(result);
        verify(productRepository, never()).save(any());
    }

    @Test
    public void testDeleteProductSuccess() {
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        when(productRepository.findByName("Laptop")).thenReturn(Optional.of(product));

        boolean result = productService.deleteProduct("Laptop", true);

        assertTrue(result);
        verify(productRepository).delete(product);
    }

    @Test
    public void testDeleteProductNotConfirmed() {
        boolean result = productService.deleteProduct("Laptop", false);

        assertFalse(result);
        verify(productRepository, never()).findByName(any());
        verify(productRepository, never()).delete(any());
    }

    @Test
    public void testDeleteProductNotFound() {
        when(productRepository.findByName("Laptop")).thenReturn(Optional.empty());

        boolean result = productService.deleteProduct("Laptop", true);

        assertFalse(result);
        verify(productRepository, never()).delete(any());
    }

    @Test
    public void testGetProductByIdSuccess() {
        UUID productId = UUID.randomUUID();
        Product product = new Product("Laptop", "Electronics", 10, 999.99);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(productId.toString());

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    public void testGetProductByIdInvalidUUID() {
        Optional<Product> result = productService.getProductById("invalid-uuid");

        assertTrue(result.isEmpty());
        verify(productRepository, never()).findById(any());
    }

    @Test
    public void testGetProductByIdNotFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(productId.toString());

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllProducts() {
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