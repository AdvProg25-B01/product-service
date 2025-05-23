package id.ac.ui.cs.advprog.productservice.model;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transaction_items")
public class TransactionItem {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "subtotal", nullable = false)
    private double subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    public TransactionItem() {
        this.id = UUID.randomUUID().toString();
    }

    public TransactionItem(Product product, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.product = product;
        this.quantity = quantity;
        this.subtotal = product.getPrice() * quantity;
    }

    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
        this.subtotal = product.getPrice() * quantity;
    }

    @PrePersist
    @PreUpdate
    protected void calculateSubtotal() {
        if (product != null) {
            this.subtotal = product.getPrice() * quantity;
        }
    }
}