package id.ac.ui.cs.advprog.productservice.productmanagement.model;

import lombok.Setter;

public class Product {
    private String name;
    @Setter
    private String category;
    @Setter
    private double price;

    public Product(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }
}