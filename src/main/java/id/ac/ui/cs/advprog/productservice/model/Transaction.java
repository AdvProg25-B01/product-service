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
    }

    public double getTotalAmount() {
    }
}
