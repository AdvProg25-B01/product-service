package id.ac.ui.cs.advprog.productservice.dto;

import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.model.TransactionItem;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDTOTest {

    private Transaction transaction;
    private Product product1;
    private Product product2;
    private Date createdAt;
    private Date updatedAt;

    @BeforeEach
    void setUp() {
        createdAt = new Date();
        updatedAt = new Date(createdAt.getTime() + 3600000); // 1 hour later

        product1 = new Product("Product 1", "Category 1", 10, 100.0);
        product2 = new Product("Product 2", "Category 2", 20, 200.0);

        List<TransactionItem> items = new ArrayList<>();
        items.add(new TransactionItem(product1, 2));
        items.add(new TransactionItem(product2, 1));

        transaction = new Transaction("test-id", "customer-123", items, "CASH", TransactionStatus.PENDING);
        transaction.setCreatedAt(createdAt);
        transaction.setUpdatedAt(updatedAt);
        transaction.calculateTotalAmount(); // Ensure total amount is calculated
    }

    @Test
    void fromTransaction_ShouldMapAllFields() {
        TransactionDTO dto = TransactionDTO.fromTransaction(transaction);

        assertEquals("test-id", dto.getId());
        assertEquals("customer-123", dto.getCustomerId());
        assertEquals(2, dto.getItems().size());
        assertEquals(400.0, dto.getTotalAmount(), 0.001); // (100*2) + (200*1)
        assertEquals("CASH", dto.getPaymentMethod());
        assertEquals(TransactionStatus.PENDING, dto.getStatus());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
    }

    @Test
    void fromTransaction_ShouldHandleEmptyItems() {
        Transaction emptyTransaction = new Transaction("empty-id", "customer-456", new ArrayList<>(), "CARD", TransactionStatus.COMPLETED);
        emptyTransaction.setCreatedAt(createdAt);
        emptyTransaction.setUpdatedAt(updatedAt);

        TransactionDTO dto = TransactionDTO.fromTransaction(emptyTransaction);

        assertEquals("empty-id", dto.getId());
        assertTrue(dto.getItems().isEmpty());
        assertEquals(0.0, dto.getTotalAmount(), 0.001);
    }

    @Test
    void fromTransaction_ShouldMapNestedItems() {
        TransactionDTO dto = TransactionDTO.fromTransaction(transaction);

        TransactionDTO.TransactionItemDTO item1Dto = dto.getItems().get(0);
        assertEquals(product1.getId(), item1Dto.getProductId());
        assertEquals("Product 1", item1Dto.getProductName());
        assertEquals(100.0, item1Dto.getPrice(), 0.001);
        assertEquals(2, item1Dto.getQuantity());
        assertEquals(200.0, item1Dto.getSubtotal(), 0.001);

        TransactionDTO.TransactionItemDTO item2Dto = dto.getItems().get(1);
        assertEquals(product2.getId(), item2Dto.getProductId());
        assertEquals("Product 2", item2Dto.getProductName());
        assertEquals(200.0, item2Dto.getPrice(), 0.001);
        assertEquals(1, item2Dto.getQuantity());
        assertEquals(200.0, item2Dto.getSubtotal(), 0.001);
    }

    @Test
    void transactionItemDTO_fromTransactionItem_ShouldMapAllFields() {
        TransactionItem item = new TransactionItem(product1, 3);

        TransactionDTO.TransactionItemDTO dto = TransactionDTO.TransactionItemDTO.fromTransactionItem(item);

        assertEquals(product1.getId(), dto.getProductId());
        assertEquals("Product 1", dto.getProductName());
        assertEquals(100.0, dto.getPrice(), 0.001);
        assertEquals(3, dto.getQuantity());
        assertEquals(300.0, dto.getSubtotal(), 0.001);
    }

    @Test
    void transactionDTO_SettersAndGetters() {
        TransactionDTO dto = new TransactionDTO();
        List<TransactionDTO.TransactionItemDTO> items = new ArrayList<>();
        TransactionDTO.TransactionItemDTO itemDto = new TransactionDTO.TransactionItemDTO();
        items.add(itemDto);

        dto.setId("new-id");
        dto.setCustomerId("new-customer");
        dto.setItems(items);
        dto.setTotalAmount(750.0);
        dto.setPaymentMethod("INSTALLMENT");
        dto.setStatus(TransactionStatus.IN_PROGRESS);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);

        assertEquals("new-id", dto.getId());
        assertEquals("new-customer", dto.getCustomerId());
        assertEquals(items, dto.getItems());
        assertEquals(750.0, dto.getTotalAmount(), 0.001);
        assertEquals("INSTALLMENT", dto.getPaymentMethod());
        assertEquals(TransactionStatus.IN_PROGRESS, dto.getStatus());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
    }

    @Test
    void transactionItemDTO_SettersAndGetters() {
        TransactionDTO.TransactionItemDTO dto = new TransactionDTO.TransactionItemDTO();

        dto.setProductId("prod-id");
        dto.setProductName("Test Product");
        dto.setPrice(150.0);
        dto.setQuantity(5);
        dto.setSubtotal(750.0);

        assertEquals("prod-id", dto.getProductId());
        assertEquals("Test Product", dto.getProductName());
        assertEquals(150.0, dto.getPrice(), 0.001);
        assertEquals(5, dto.getQuantity());
        assertEquals(750.0, dto.getSubtotal(), 0.001);
    }

    @Test
    void equals_HashCode_ToString_ShouldWork() {
        TransactionDTO dto1 = TransactionDTO.fromTransaction(transaction);
        TransactionDTO dto2 = TransactionDTO.fromTransaction(transaction);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setId("different-id");
        assertNotEquals(dto1, dto2);

        assertDoesNotThrow(() -> dto1.toString());

        TransactionDTO.TransactionItemDTO item1 = dto1.getItems().get(0);
        TransactionDTO.TransactionItemDTO item2 = new TransactionDTO.TransactionItemDTO();
        item2.setProductId(item1.getProductId());
        item2.setProductName(item1.getProductName());
        item2.setPrice(item1.getPrice());
        item2.setQuantity(item1.getQuantity());
        item2.setSubtotal(item1.getSubtotal());

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertDoesNotThrow(() -> item1.toString());
    }
}