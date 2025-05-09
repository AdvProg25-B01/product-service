package id.ac.ui.cs.advprog.productservice.dto;

import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.model.TransactionItem;
import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TransactionDTO {
    private String id;
    private String customerId;
    private List<TransactionItemDTO> items;
    private double totalAmount;
    private String paymentMethod;
    private TransactionStatus status;
    private Date createdAt;
    private Date updatedAt;

    public static TransactionDTO fromTransaction(Transaction transaction) {

    }

    @Data
    public static class TransactionItemDTO {
        private String productId;
        private String productName;
        private double price;
        private int quantity;
        private double subtotal;

        public static TransactionItemDTO fromTransactionItem(TransactionItem item) {
        }
    }
}