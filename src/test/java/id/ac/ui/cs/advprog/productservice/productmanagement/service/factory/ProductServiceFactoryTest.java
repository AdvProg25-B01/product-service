package id.ac.ui.cs.advprog.productservice.productmanagement.service.factory;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.repository.ProductRepository;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceFactoryTest {

    private ProductServiceFactory productServiceFactory;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productServiceFactory = new DefaultProductServiceFactory(productRepository);
    }

    @Test
    void testCreateProductService() {
        ProductService productService = productServiceFactory.createProductService();

        // Check that productService is not null and is an instance of ProductService
        assertNotNull(productService);
        assertTrue(productService instanceof ProductService);

        // Verify that the repository is injected correctly (via the factory)
        assertEquals(productRepository, ((ProductServiceImpl) productService).getRepository());
    }
}