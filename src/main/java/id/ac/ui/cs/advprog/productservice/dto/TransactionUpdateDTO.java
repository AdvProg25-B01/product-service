package id.ac.ui.cs.advprog.productservice.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TransactionUpdateDTO {
    private String customerId;
    private Map<String, Integer> productQuantities;
    private String paymentMethod;
}