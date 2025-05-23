package id.ac.ui.cs.advprog.productservice.productmanagement.controller;

import id.ac.ui.cs.advprog.productservice.productmanagement.factory.ProductFactory;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult; // Added for MvcResult

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch; // Added for asyncDispatch

@ActiveProfiles("test")
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private UUID testProductId;

    @BeforeEach
    public void setUp() {
        testProductId = UUID.randomUUID();
        // Assuming ProductFactory does not set ID, or it's set by constructor/service.
        // For consistency, we'll create products in tests and let the controller's factory call handle its instance.
        testProduct = ProductFactory.createProduct("Test Laptop", "Electronics", 10, 999.99);
        testProduct.setId(testProductId); // Manually set ID for products used in 'getAllProducts' mocks
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
                .andExpect(jsonPath("$[0].price").value(999.99))
                .andExpect(jsonPath("$[0].id").value(testProductId.toString())); // Assuming ID is serialized
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductByName() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/product/Test Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Laptop"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.id").value(testProductId.toString()));
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
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product with ID '" + productId + "' not found")); // Match controller message
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductByIdInvalidFormat() throws Exception {
        mockMvc.perform(get("/product/id/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid UUID format: invalid-uuid")); // Match controller message
        verify(productService, times(0)).getAllProducts();
    }

    @Test
    public void testCreateProductSuccess() throws Exception {
        when(productService.addProduct(any(Product.class), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(true));

        Product newProduct = ProductFactory.createProduct("New Laptop", "Electronics", 5, 799.99);
        // Assuming the 'product' returned by the controller (from its own ProductFactory call)
        // will have an ID, either from Product constructor or factory.

        MvcResult mvcResult = mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Laptop"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.stock").value(5))
                .andExpect(jsonPath("$.price").value(799.99))
                .andExpect(jsonPath("$.id").isNotEmpty()); // Check if ID exists and is not empty
        verify(productService, times(1)).addProduct(any(Product.class), eq(true));
    }

    @Test
    public void testCreateProductFailureFromServiceLogic() throws Exception {
        when(productService.addProduct(any(Product.class), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(false));

        // Corrected: Product must be valid for ProductFactory inside the controller
        Product productToSubmit = ProductFactory.createProduct("ServiceFailProduct", "Electronics", 5, 99.99); // Valid price

        MvcResult mvcResult = mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productToSubmit)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product is invalid or could not be created"));
        verify(productService, times(1)).addProduct(any(Product.class), eq(true));
    }

    @Test
    public void testCreateProductInvalidInputFormat() throws Exception {
        // This test covers the controller's immediate try-catch block for IllegalArgumentException from ProductFactory
        MvcResult mvcResult = mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"category\":\"Electronics\",\"stock\":5,\"price\":799.99}")) // Empty name
                .andExpect(request().asyncStarted()) // Controller returns CompletableFuture
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product is invalid or could not be created"));
        verify(productService, times(0)).addProduct(any(Product.class), anyBoolean());
    }


    @Test
    public void testEditProductSuccess() throws Exception {
        when(productService.editProduct(any(Product.class), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(true));

        Product updatedProduct = ProductFactory.createProduct("Updated Laptop", "Computing", 15, 1199.99);
        updatedProduct.setId(testProductId); // Set ID for the product sent in body

        MvcResult mvcResult = mockMvc.perform(put("/product/edit/{name}", "Test Laptop") // Path variable name
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                // Controller returns the product from the request body upon success.
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.id").value(testProductId.toString())); // Check ID from request body
        verify(productService, times(1)).editProduct(any(Product.class), eq(true));
    }

    @Test
    public void testEditProductFailure() throws Exception {
        when(productService.editProduct(any(Product.class), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(false));

        Product updatedProduct = ProductFactory.createProduct("Updated Laptop", "Computing", 15, 1199.99);
        updatedProduct.setId(testProductId); // ID is part of the body

        MvcResult mvcResult = mockMvc.perform(put("/product/edit/{name}", "NonExistentProductToEdit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Edit failed. Product could not be updated or not found."));
        verify(productService, times(1)).editProduct(any(Product.class), eq(true));
    }

    @Test
    public void testDeleteProductSuccess() throws Exception {
        when(productService.deleteProduct(eq("Test Laptop"), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(true));

        MvcResult mvcResult = mockMvc.perform(delete("/product/delete/{name}", "Test Laptop"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
        verify(productService, times(1)).deleteProduct(eq("Test Laptop"), eq(true));
    }

    @Test
    public void testDeleteProductFailure() throws Exception {
        when(productService.deleteProduct(eq("NonExistent"), eq(true)))
                .thenReturn(CompletableFuture.completedFuture(false));

        MvcResult mvcResult = mockMvc.perform(delete("/product/delete/{name}", "NonExistent"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product deletion failed or product not found."));
        verify(productService, times(1)).deleteProduct(eq("NonExistent"), eq(true));
    }
}