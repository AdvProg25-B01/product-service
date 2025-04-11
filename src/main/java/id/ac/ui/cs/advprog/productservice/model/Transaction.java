package id.ac.ui.cs.advprog.productservice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Transaction {
    private String id;
    private List<Product> products;

    public Transaction(String id, List<Product> products) {
        this.id = id;
        this.products = products;
    }

    public double getTotalAmount() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }
}
