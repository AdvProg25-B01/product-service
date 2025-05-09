package id.ac.ui.cs.advprog.productservice.model.builder;

import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.model.TransactionItem;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionBuilderTest {

    private TransactionBuilder builder;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        builder = new TransactionBuilder();
        product1 = new Product("Product 1", "Category 1", 10, 100.0);
        product2 = new Product("Product 2", "Category 2", 20, 200.0);
    }

    @Test
    void testDefaultConstruction() {
        Transaction transaction = builder.build();

        assertNotNull(transaction.getId());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertTrue(transaction.getItems().isEmpty());
        assertEquals(0.0, transaction.getTotalAmount());
    }

    @Test
    void testWithId() {
        Transaction transaction = builder
                .withId("custom-id")
                .build();

        assertEquals("custom-id", transaction.getId());
    }

    @Test
    void testWithCustomerId() {
        Transaction transaction = builder
                .withCustomerId("customer-123")
                .build();

        assertEquals("customer-123", transaction.getCustomerId());
    }

    @Test
    void testWithSingleItem() {
        Transaction transaction = builder
                .withItem(product1, 2)
                .build();

        assertEquals(1, transaction.getItems().size());
        assertEquals(product1, transaction.getItems().get(0).getProduct());
        assertEquals(2, transaction.getItems().get(0).getQuantity());
        assertEquals(200.0, transaction.getTotalAmount());
    }

    @Test
    void testWithMultipleItems() {
        Transaction transaction = builder
                .withItem(product1, 2)
                .withItem(product2, 1)
                .build();

        assertEquals(2, transaction.getItems().size());
        assertEquals(product1, transaction.getItems().get(0).getProduct());
        assertEquals(product2, transaction.getItems().get(1).getProduct());
        assertEquals(400.0, transaction.getTotalAmount());
    }

    @Test
    void testWithItemsList() {
        List<TransactionItem> items = new ArrayList<>();
        items.add(new TransactionItem(product1, 3));
        items.add(new TransactionItem(product2, 2));

        Transaction transaction = builder
                .withItems(items)
                .build();

        assertEquals(2, transaction.getItems().size());
        assertEquals(product1, transaction.getItems().get(0).getProduct());
        assertEquals(3, transaction.getItems().get(0).getQuantity());
        assertEquals(product2, transaction.getItems().get(1).getProduct());
        assertEquals(2, transaction.getItems().get(1).getQuantity());
        assertEquals(700.0, transaction.getTotalAmount());
    }

    @Test
    void testWithPaymentMethod() {
        Transaction transaction = builder
                .withPaymentMethod("CREDIT_CARD")
                .build();

        assertEquals("CREDIT_CARD", transaction.getPaymentMethod());
    }

    @Test
    void testWithStatus() {
        Transaction transaction = builder
                .withStatus(TransactionStatus.COMPLETED)
                .build();

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
    }

    @Test
    void testCompleteBuilder() {
        Transaction transaction = builder
                .withId("txn-12345")
                .withCustomerId("cust-67890")
                .withItem(product1, 2)
                .withItem(product2, 3)
                .withPaymentMethod("CASH")
                .withStatus(TransactionStatus.IN_PROGRESS)
                .build();

        assertEquals("txn-12345", transaction.getId());
        assertEquals("cust-67890", transaction.getCustomerId());
        assertEquals(2, transaction.getItems().size());
        assertEquals("CASH", transaction.getPaymentMethod());
        assertEquals(TransactionStatus.IN_PROGRESS, transaction.getStatus());
        assertEquals(800.0, transaction.getTotalAmount());
    }

    @Test
    void testChainedWithItems() {
        List<TransactionItem> items1 = new ArrayList<>();
        items1.add(new TransactionItem(product1, 1));

        List<TransactionItem> items2 = new ArrayList<>();
        items2.add(new TransactionItem(product2, 2));

        Transaction transaction = builder
                .withItems(items1)
                .withItems(items2)
                .build();

        assertEquals(2, transaction.getItems().size());
        assertEquals(product1, transaction.getItems().get(0).getProduct());
        assertEquals(product2, transaction.getItems().get(1).getProduct());
        assertEquals(500.0, transaction.getTotalAmount());
    }

    @Test
    void testCombinedItemAddition() {
        List<TransactionItem> items = new ArrayList<>();
        items.add(new TransactionItem(product1, 1));

        Transaction transaction = builder
                .withItems(items)
                .withItem(product2, 2)
                .build();

        assertEquals(2, transaction.getItems().size());
        assertEquals(product1, transaction.getItems().get(0).getProduct());
        assertEquals(product2, transaction.getItems().get(1).getProduct());
        assertEquals(500.0, transaction.getTotalAmount());
    }

    @Test
    void testCalculateTotalAmount() {
        Transaction transaction = builder
                .withItem(product1, 5)
                .withItem(product2, 2)
                .build();

        assertEquals(900.0, transaction.getTotalAmount());
    }
}