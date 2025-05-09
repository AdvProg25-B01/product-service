package id.ac.ui.cs.advprog.productservice.model;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionItem {
    private Product product;
    private int quantity;
    private double subtotal;
    @Setter
    private Transaction transaction;

    public TransactionItem(Product product, int quantity) {
    }

    public void updateQuantity(int newQuantity) {
    }
}