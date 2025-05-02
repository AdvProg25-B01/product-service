package id.ac.ui.cs.advprog.productservice.productmanagement.controller;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    // Use LENIENT strictness to allow unused stubs
    @Mock(lenient = true)
    private ProductService productService;

    private MockMvc mockMvc;



    @InjectMocks
    private ProductController productController;

    private Product product;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        product = new Product("Laptop", "Elektronik", 15000000.0);
    }

    // Test for Creating Product Success
    @Test
    void testCreateProductSuccess() throws Exception {
        when(productService.addProduct(any(Product.class), anyBoolean())).thenReturn(true);

        mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Laptop\",\"category\":\"Elektronik\",\"price\":15000000.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.category").value("Elektronik"))
                .andExpect(jsonPath("$.price").value(15000000.0));
    }

    // Test for Creating Product Failure
    @Test
    void testCreateProductFailure() throws Exception {
        when(productService.addProduct(any(Product.class), anyBoolean())).thenReturn(false);

        mockMvc.perform(post("/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Laptop\",\"category\":\"Elektronik\",\"price\":15000000.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product is invalid or could not be created"));
    }

    // Test for Listing Products
    @Test
    void testListProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/product/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].category").value("Elektronik"))
                .andExpect(jsonPath("$[0].price").value(15000000.0));
    }

    // Test for Getting a Single Product
    @Test
    void testGetProduct() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/product/Laptop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.category").value("Elektronik"))
                .andExpect(jsonPath("$.price").value(15000000.0));
    }

    // Test for Getting a Non-existent Product
    @Test
    void testGetProductNotFound() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/product/NonExistentProduct")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Test for Updating Product Success
    @Test
    void testEditProductSuccess() throws Exception {
        when(productService.editProduct(any(Product.class), anyBoolean())).thenReturn(true);

        mockMvc.perform(put("/product/edit/Laptop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Laptop\",\"category\":\"Gadget\",\"price\":14000000.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.category").value("Gadget"))
                .andExpect(jsonPath("$.price").value(14000000.0));
    }

    // Test for Updating Product Failure
    @Test
    void testEditProductFailure() throws Exception {
        when(productService.editProduct(any(Product.class), anyBoolean())).thenReturn(false);

        mockMvc.perform(put("/product/edit/Laptop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Laptop\",\"category\":\"Gadget\",\"price\":14000000.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Edit failed. Product could not be updated."));
    }

    // Test for Deleting Product Success
    @Test
    void testDeleteProductSuccess() throws Exception {
        when(productService.deleteProduct(anyString(), anyBoolean())).thenReturn(true);

        mockMvc.perform(delete("/product/delete/Laptop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
    }

    // Test for Deleting Product Failure
    @Test
    void testDeleteProductFailure() throws Exception {
        when(productService.deleteProduct(anyString(), anyBoolean())).thenReturn(false);

        mockMvc.perform(delete("/product/delete/Laptop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product deletion failed."));
    }
}