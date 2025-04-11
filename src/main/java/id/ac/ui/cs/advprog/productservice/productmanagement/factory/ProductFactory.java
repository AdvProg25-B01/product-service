package id.ac.ui.cs.advprog.productservice.productmanagement.factory;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;

public class ProductFactory {

    public static Product createProduct(String name, String category, double price) {
        // You can also include validation or other logic here before returning the product
        if (name == null || name.isEmpty() || category == null || category.isEmpty() || price <= 0) {
            throw new IllegalArgumentException("Invalid product data");
        }
        return new Product(name, category, price);
    }
}
