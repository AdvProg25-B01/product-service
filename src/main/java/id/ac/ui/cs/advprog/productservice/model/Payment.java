package id.ac.ui.cs.advprog.productservice.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class Payment {
    private String id;
    private String customerId;
    private double amount;
    private String method;
    private String status;
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