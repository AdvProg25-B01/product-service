package id.ac.ui.cs.advprog.productservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "status", nullable = false)
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    public Payment(String id, String customerId, double amount, String method, String status, Date createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Payment() {
        this.createdAt = new Date();
    }
}
