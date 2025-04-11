package id.ac.ui.cs.advprog.productservice.factory;

import id.ac.ui.cs.advprog.productservice.model.Product;
import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.model.builder.TransactionBuilder;

import java.util.List;

public class TransactionFactory {

    public static Transaction create(String id, List<Product> products) {
        TransactionBuilder builder = new TransactionBuilder().withId(id);
        for (Product product : products) {
            builder.withProduct(product);
        }
        return builder.build();
    }
}
