package id.ac.ui.cs.advprog.productservice.dto;

import lombok.Data;

@Data
public class TransactionUpdateDTO {
    private String customerId;
    private Double amount;
    private String paymentMethod;
}