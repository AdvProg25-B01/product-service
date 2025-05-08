package id.ac.ui.cs.advprog.productservice.model.builder;

import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;

import java.util.List;
import java.util.ArrayList;

public class TransactionBuilder {
    private String id;
    private List<Product> products = new ArrayList<>();

    public TransactionBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public TransactionBuilder withProduct(Product product) {
        this.products.add(product);
        return this;
    }

    public Transaction build() {
        return new Transaction(id, products);
    }
}