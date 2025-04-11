package id.ac.ui.cs.advprog.productservice.productmanagement.controller;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Form to input a new product
    @GetMapping("/create")
    public String createProductPage(Model model) {
        model.addAttribute("product", new Product("", "", 0));
        return "CreateProduct";
    }

    // Process saving a new product
    @PostMapping("/create")
    public String createProduct(@ModelAttribute Product product,
                                @RequestParam(defaultValue = "false") boolean confirm,
                                Model model) {
        boolean success = productService.addProduct(product, confirm);

        if (!success) {
            model.addAttribute("error", "Product is invalid or not confirmed.");
            return "CreateProduct";
        }

        return "redirect:/product/list";
    }

    // Display all products
    @GetMapping("/list")
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "ProductList";
    }

    // Form to edit an existing product
    @GetMapping("/edit/{name}")
    public String editProductPage(@PathVariable String name, Model model) {
        List<Product> products = productService.getAllProducts();
        Product product = products.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (product == null) {
            model.addAttribute("error", "Product not found.");
            return "redirect:/product/list";
        }

        model.addAttribute("product", product);
        return "EditProduct";
    }

    // Process editing an existing product
    @PostMapping("/edit")
    public String editProduct(@ModelAttribute Product product,
                              @RequestParam(defaultValue = "false") boolean confirm,
                              Model model) {
        boolean success = productService.editProduct(product, confirm);

        if (!success) {
            model.addAttribute("error", "Edit failed. Product not confirmed.");
            return "EditProduct";
        }

        return "redirect:/product/list";
    }

    // Delete a product
    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("name") String name,
                                @RequestParam(defaultValue = "false") boolean confirm,
                                Model model) {
        boolean success = productService.deleteProduct(name, confirm);

        if (!success) {
            model.addAttribute("error", "Product deletion failed. Not confirmed.");
        }

        return "redirect:/product/list";
    }
}