package id.ac.ui.cs.advprog.productservice.model.builder;

import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.model.Product;

import java.util.List;
import java.util.ArrayList;

public class TransactionBuilder {
    private String id;
    private List<Product> products = new ArrayList<>();

    public TransactionBuilder withId(String id) {
    }

    public TransactionBuilder withProduct(Product product) {
    }

    public Transaction build() {
    }
}