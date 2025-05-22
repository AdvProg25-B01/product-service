package id.ac.ui.cs.advprog.productservice.productmanagement.controller;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product testProduct;
    private UUID testProductId;

    @BeforeEach
    public void setUp() {
        testProductId = UUID.randomUUID();
        testProduct = new Product("Test Laptop", "Electronics", 10, 999.99);
        testProduct.setId(testProductId);
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
    }

    @Test
    public void testGetProductByName() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/product/Test Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Laptop"))
                .andExpect(jsonPath("$.category").value("Electronics"));
    }

    @Test
    public void testGetProductByNameNotFound() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/product/NonExistent"))
                .andExpect(status().isNotFound());
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
    }

    @Test
    public void testGetProductByIdNotFound() throws Exception {
        String productId = UUID.randomUUID().toString();
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/product/id/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProductByIdInvalidFormat() throws Exception {
        mockMvc.perform(get("/product/id/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateProduct() throws Exception {
        when(productService.addProduct(any(Product.class), eq(true))).thenReturn(true);

        mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Laptop\",\"category\":\"Electronics\",\"stock\":5,\"price\":799.99}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateProductFailure() throws Exception {
        when(productService.addProduct(any(Product.class), eq(true))).thenReturn(false);

        mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Invalid Product\",\"category\":\"Electronics\",\"stock\":5,\"price\":-100}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEditProduct() throws Exception {
        when(productService.editProduct(any(Product.class), eq(true))).thenReturn(true);

        mockMvc.perform(put("/product/edit/Test Laptop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Laptop\",\"category\":\"Computing\",\"stock\":15,\"price\":1199.99}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEditProductFailure() throws Exception {
        when(productService.editProduct(any(Product.class), eq(true))).thenReturn(false);

        mockMvc.perform(put("/product/edit/NonExistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Laptop\",\"category\":\"Computing\",\"stock\":15,\"price\":1199.99}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        when(productService.deleteProduct(eq("Test Laptop"), eq(true))).thenReturn(true);

        mockMvc.perform(delete("/product/delete/Test Laptop"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProductFailure() throws Exception {
        when(productService.deleteProduct(eq("NonExistent"), eq(true))).thenReturn(false);

        mockMvc.perform(delete("/product/delete/NonExistent"))
                .andExpect(status().isBadRequest());
    }
}