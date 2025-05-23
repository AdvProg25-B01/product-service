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
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setCustomerId(transaction.getCustomerId());
        dto.setItems(transaction.getItems().stream()
                .map(TransactionItemDTO::fromTransactionItem)
                .collect(Collectors.toList()));
        dto.setTotalAmount(transaction.getTotalAmount());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setStatus(transaction.getStatus());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());
        return dto;
    }

    @Data
    public static class TransactionItemDTO {
        private String productId;
        private String productName;
        private double price;
        private int quantity;
        private double subtotal;

        public static TransactionItemDTO fromTransactionItem(TransactionItem item) {
            TransactionItemDTO dto = new TransactionItemDTO();
            dto.setProductId(String.valueOf(item.getProduct().getId()));
            dto.setProductName(item.getProduct().getName());
            dto.setPrice(item.getProduct().getPrice());
            dto.setQuantity(item.getQuantity());
            dto.setSubtotal(item.getSubtotal());
            return dto;
        }
    }
}