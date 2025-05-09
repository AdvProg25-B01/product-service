package id.ac.ui.cs.advprog.productservice.model;

import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Transaction {
    private String id;
    private String customerId;
    private List<TransactionItem> items = new ArrayList<>();
    private double totalAmount;
    private String paymentMethod;
    private TransactionStatus status;
    private Date createdAt;
    private Date updatedAt;

    private Payment payment;

    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.status = TransactionStatus.PENDING;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Transaction(String id, String customerId, List<TransactionItem> items,
                       String paymentMethod, TransactionStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        calculateTotalAmount();
    }

    public void calculateTotalAmount() {
        this.totalAmount = items.stream().mapToDouble(TransactionItem::getSubtotal).sum();
    }

    public void addItem(TransactionItem item) {
        for (TransactionItem existingItem : items) {
            if (existingItem.getProduct().getId().equals(item.getProduct().getId())) {
                existingItem.updateQuantity(existingItem.getQuantity() + item.getQuantity());
                calculateTotalAmount();
                return;
            }
        }
        items.add(item);
        calculateTotalAmount();
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
        calculateTotalAmount();
    }

    public void updateItemQuantity(String productId, int newQuantity) {
        for (TransactionItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                if (newQuantity <= 0) {
                    removeItem(productId);
                } else {
                    item.updateQuantity(newQuantity);
                }
                calculateTotalAmount();
                return;
            }
        }
    }

    public void complete() {
        if (this.status == TransactionStatus.PENDING) {
            this.status = TransactionStatus.COMPLETED;
            this.updatedAt = new Date();
        }
    }

    public void markInProgress() {
        if (this.status == TransactionStatus.PENDING) {
            this.status = TransactionStatus.IN_PROGRESS;
            this.updatedAt = new Date();
        }
    }

    public void cancel() {
        if (this.status != TransactionStatus.CANCELLED) {
            this.status = TransactionStatus.CANCELLED;
            this.updatedAt = new Date();
        }
    }
}
