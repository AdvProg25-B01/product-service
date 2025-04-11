package id.ac.ui.cs.advprog.productservice.productmanagement.controller;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product product;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        product = new Product("Laptop", "Elektronik", 15000000.0);
    }

    // Test for Create Product Page
    @Test
    void testCreateProductPage() throws Exception {
        mockMvc.perform(get("/product/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("CreateProduct"))
                .andExpect(model().attributeExists("product"));
    }

    // Test for Creating Product Success
    @Test
    void testCreateProductSuccess() throws Exception {
        when(productService.addProduct(Mockito.any(Product.class), Mockito.anyBoolean())).thenReturn(true);

        mockMvc.perform(post("/product/create")
                        .param("name", "Laptop")
                        .param("category", "Elektronik")
                        .param("price", "15000000.0")
                        .param("confirm", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));
    }

    // Test for Creating Product Failure (Invalid Data / Not Confirmed)
    @Test
    void testCreateProductFailure() throws Exception {
        when(productService.addProduct(Mockito.any(Product.class), Mockito.anyBoolean())).thenReturn(false);

        mockMvc.perform(post("/product/create")
                        .param("name", "Laptop")
                        .param("category", "Elektronik")
                        .param("price", "15000000.0")
                        .param("confirm", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("CreateProduct"))
                .andExpect(model().attributeExists("error"));
    }

    // Test for Listing Products
    @Test
    void testListProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("ProductList"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", products));
    }

    // Test for Edit Product Page
    @Test
    void testEditProductPage() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/product/edit/Laptop"))
                .andExpect(status().isOk())
                .andExpect(view().name("EditProduct"))
                .andExpect(model().attributeExists("product"));
    }

    // Test for Edit Product Failure (Product Not Found)
    @Test
    void testEditProductFailure() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/product/edit/NonExistentProduct"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));
    }

    // Test for Editing Product Success
    @Test
    void testEditProductSuccess() throws Exception {
        when(productService.editProduct(Mockito.any(Product.class), Mockito.anyBoolean())).thenReturn(true);

        mockMvc.perform(post("/product/edit")
                        .param("name", "Laptop")
                        .param("category", "Gadget")
                        .param("price", "14000000.0")
                        .param("confirm", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));
    }

    // Test for Editing Product Failure (Not Confirmed)
    @Test
    void testEditProductFailure() throws Exception {
        when(productService.editProduct(Mockito.any(Product.class), Mockito.anyBoolean())).thenReturn(false);

        mockMvc.perform(post("/product/edit")
                        .param("name", "Laptop")
                        .param("category", "Gadget")
                        .param("price", "14000000.0")
                        .param("confirm", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("EditProduct"))
                .andExpect(model().attributeExists("error"));
    }

    // Test for Deleting Product Success
    @Test
    void testDeleteProductSuccess() throws Exception {
        when(productService.deleteProduct(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(true);

        mockMvc.perform(post("/product/delete")
                        .param("name", "Laptop")
                        .param("confirm", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));
    }

    // Test for Deleting Product Failure (Not Confirmed)
    @Test
    void testDeleteProductFailure() throws Exception {
        when(productService.deleteProduct(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);

        mockMvc.perform(post("/product/delete")
                        .param("name", "Laptop")
                        .param("confirm", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));
    }
}