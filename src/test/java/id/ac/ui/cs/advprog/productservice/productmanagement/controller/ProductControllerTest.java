package id.ac.ui.cs.advprog.productservice.productmanagement.controller;

import id.ac.ui.cs.advprog.productservice.productmanagement.factory.ProductFactory; // Required for object mapper content
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper; // New import for JSON
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture; // New import

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify; // Verify imports for explicit checks
import static org.mockito.Mockito.times; // Verify imports for explicit checks
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // Autowire ObjectMapper

    private Product testProduct;
    private UUID testProductId;

    @BeforeEach
    public void setUp() {
        testProductId = UUID.randomUUID();
        // Use ProductFactory if possible to ensure consistency with controller's creation logic
        testProduct = ProductFactory.createProduct("Test Laptop", "Electronics", 10, 999.99);
        testProduct.setId(testProductId); // Manually set ID as factory doesn't do it
    }

    @Test
    public void testListProducts() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Laptop"))
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$[0].stock").value(10))
                .andExpect(jsonPath("$[0].price").value(999.99));
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductByName() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/product/Test Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Laptop"))
                .andExpect(jsonPath("$.category").value("Electronics"));
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductByNameNotFound() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/product/NonExistent"))
                .andExpect(status().isNotFound());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductByIdSuccess() throws Exception {
        String productId = testProductId.toString();
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/product/id/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Laptop"))
                .andExpect(jsonPath("$.id").value(testProductId.toString()));
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductByIdNotFound() throws Exception {
        String productId = UUID.randomUUID().toString();
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/product/id/{id}", productId))
                .andExpect(status().isNotFound());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductByIdInvalidFormat() throws Exception {
        // No call to getAllProducts should happen if UUID is invalid
        mockMvc.perform(get("/product/id/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
        verify(productService, times(0)).getAllProducts(); // Ensure service not called
    }

    @Test
    public void testCreateProductSuccess() throws Exception {
        // Mock the async service method return
        when(productService.addProduct(any(Product.class), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(true));

        Product newProduct = ProductFactory.createProduct("New Laptop", "Electronics", 5, 799.99);

        mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct))) // Use ObjectMapper
                .andExpect(status().isCreated());
        verify(productService, times(1)).addProduct(any(Product.class), eq(true));
    }

    @Test
    public void testCreateProductFailureFromServiceLogic() throws Exception {
        // Mock the async service method return for failure
        when(productService.addProduct(any(Product.class), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(false));

        Product invalidProduct = ProductFactory.createProduct("Invalid Product", "Electronics", 5, -100.0);

        mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct))) // Use ObjectMapper
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product is invalid or could not be created")); // Specific message
        verify(productService, times(1)).addProduct(any(Product.class), eq(true));
    }

    @Test
    public void testCreateProductInvalidInputFormat() throws Exception {
        // This test covers the controller's immediate try-catch block for IllegalArgumentException from ProductFactory
        // No service call should happen here.
        mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"category\":\"Electronics\",\"stock\":5,\"price\":799.99}")) // Empty name
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product is invalid or could not be created"));
        verify(productService, times(0)).addProduct(any(Product.class), anyBoolean());
    }


    @Test
    public void testEditProductSuccess() throws Exception {
        // Mock the async service method return
        when(productService.editProduct(any(Product.class), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(true));

        Product updatedProduct = ProductFactory.createProduct("Updated Laptop", "Computing", 15, 1199.99);
        updatedProduct.setId(testProductId); // Crucial: Set ID for the product sent in body

        mockMvc.perform(put("/product/edit/Test Laptop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct))) // Use ObjectMapper
                .andExpect(status().isOk());
        verify(productService, times(1)).editProduct(any(Product.class), eq(true));
    }

    @Test
    public void testEditProductFailure() throws Exception {
        // Mock the async service method return for failure
        when(productService.editProduct(any(Product.class), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(false));

        Product updatedProduct = ProductFactory.createProduct("Updated Laptop", "Computing", 15, 1199.99);
        updatedProduct.setId(testProductId); // ID here might not matter if service logic fails before lookup

        mockMvc.perform(put("/product/edit/NonExistent") // Path variable is name, but body content uses ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Edit failed. Product could not be updated or not found.")); // Specific message
        verify(productService, times(1)).editProduct(any(Product.class), eq(true));
    }

    @Test
    public void testDeleteProductSuccess() throws Exception {
        // Mock the async service method return
        when(productService.deleteProduct(eq("Test Laptop"), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(true));

        mockMvc.perform(delete("/product/delete/Test Laptop"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully")); // Specific message
        verify(productService, times(1)).deleteProduct(eq("Test Laptop"), eq(true));
    }

    @Test
    public void testDeleteProductFailure() throws Exception {
        // Mock the async service method return for failure
        when(productService.deleteProduct(eq("NonExistent"), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(false));

        mockMvc.perform(delete("/product/delete/NonExistent"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product deletion failed or product not found.")); // Specific message
        verify(productService, times(1)).deleteProduct(eq("NonExistent"), eq(true));
    }
}