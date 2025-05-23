package id.ac.ui.cs.advprog.productservice.model;

import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TransactionItem> items = new ArrayList<>();

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
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
        this.items = items != null ? items : new ArrayList<>();
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
        item.setTransaction(this); // Set bidirectional relationship
        items.add(item);
        calculateTotalAmount();
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getId().equals(UUID.fromString(productId)));
        calculateTotalAmount();
    }

    public void updateItemQuantity(String productId, int newQuantity) {
        for (TransactionItem item : items) {
            if (item.getProduct().getId().equals(UUID.fromString(productId))) {
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

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}