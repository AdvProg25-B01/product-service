package id.ac.ui.cs.advprog.productservice.productmanagement.model;

import lombok.Setter;
import lombok.Getter;
import java.util.UUID;

public class Product {
    @Getter
    private final String id = UUID.randomUUID().toString();

    @Getter
    private final String name;

    @Setter
    @Getter
    private String category;

    @Setter
    @Getter
    private int stock;

    @Setter
    @Getter
    private double price;

    public Product(String name, String category, int stock, double price) {
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.price = price;
    }
}