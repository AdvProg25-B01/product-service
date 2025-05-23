package id.ac.ui.cs.advprog.productservice.model;

import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Transaction transaction;
    private Product product1;
    private Product product2;
    private TransactionItem item1;
    private TransactionItem item2;

    @BeforeEach
    void setUp() {
        product1 = new Product("Product 1", "Category 1", 10, 100.0);
        product2 = new Product("Product 2", "Category 2", 20, 200.0);

        item1 = new TransactionItem(product1, 2);
        item2 = new TransactionItem(product2, 1);

        List<TransactionItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        transaction = new Transaction("test-id", "customer-1", items, "CASH", TransactionStatus.PENDING);
    }

    @Test
    void testDefaultConstructor() {
        Transaction newTransaction = new Transaction();

        assertNotNull(newTransaction.getId());
        assertEquals(TransactionStatus.PENDING, newTransaction.getStatus());
        assertNotNull(newTransaction.getCreatedAt());
        assertNotNull(newTransaction.getUpdatedAt());
        assertTrue(newTransaction.getItems().isEmpty());
        assertEquals(0.0, newTransaction.getTotalAmount());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals("test-id", transaction.getId());
        assertEquals("customer-1", transaction.getCustomerId());
        assertEquals(2, transaction.getItems().size());
        assertEquals("CASH", transaction.getPaymentMethod());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertNotNull(transaction.getCreatedAt());
        assertNotNull(transaction.getUpdatedAt());

        // 2 * 100.0 + 1 * 200.0 = 400.0
        assertEquals(400.0, transaction.getTotalAmount());
    }

    @Test
    void testCalculateTotalAmount() {
        assertEquals(400.0, transaction.getTotalAmount());

        Product product3 = new Product("Product 3", "Category 3", 5, 150.0);
        TransactionItem item3 = new TransactionItem(product3, 3);
        transaction.addItem(item3);

        // 400.0 + (3 * 150.0) = 850.0
        assertEquals(850.0, transaction.getTotalAmount());
    }

    @Test
    void testAddItem_NewProduct() {
        assertEquals(2, transaction.getItems().size());

        Product product3 = new Product("Product 3", "Category 3", 5, 150.0);
        TransactionItem item3 = new TransactionItem(product3, 3);
        transaction.addItem(item3);

        assertEquals(3, transaction.getItems().size());
        assertEquals(850.0, transaction.getTotalAmount());
    }

    @Test
    void testAddItem_ExistingProduct() {
        assertEquals(2, transaction.getItems().get(0).getQuantity());

        TransactionItem newItem = new TransactionItem(product1, 3);
        transaction.addItem(newItem);

        assertEquals(2, transaction.getItems().size());
        assertEquals(5, transaction.getItems().get(0).getQuantity());

        // (5 * 100.0) + (1 * 200.0) = 700.0
        assertEquals(700.0, transaction.getTotalAmount());
    }

    @Test
    void testRemoveItem() {
        assertEquals(2, transaction.getItems().size());

        transaction.removeItem(product1.getId().toString());

        assertEquals(1, transaction.getItems().size());
        assertEquals(product2.getId(), transaction.getItems().get(0).getProduct().getId());

        assertEquals(200.0, transaction.getTotalAmount());
    }

    @Test
    void testUpdateItemQuantity_Increase() {
        assertEquals(2, transaction.getItems().get(0).getQuantity());

        transaction.updateItemQuantity(product1.getId().toString(), 4);

        assertEquals(4, transaction.getItems().get(0).getQuantity());

        // (4 * 100.0) + (1 * 200.0) = 600.0
        assertEquals(600.0, transaction.getTotalAmount());
    }

    @Test
    void testUpdateItemQuantity_Decrease() {
        assertEquals(2, transaction.getItems().get(0).getQuantity());

        transaction.updateItemQuantity(product1.getId().toString(), 1);

        assertEquals(1, transaction.getItems().get(0).getQuantity());

        // (1 * 100.0) + (1 * 200.0) = 300.0
        assertEquals(300.0, transaction.getTotalAmount());
    }

    @Test
    void testUpdateItemQuantity_ZeroOrNegative() {
        assertEquals(2, transaction.getItems().size());

        transaction.updateItemQuantity(product1.getId().toString(), 0);

        assertEquals(1, transaction.getItems().size());
        assertEquals(product2.getId(), transaction.getItems().get(0).getProduct().getId());

        assertEquals(200.0, transaction.getTotalAmount());
    }

    @Test
    void testComplete_FromPending() throws InterruptedException {
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());

        Date initialUpdate = transaction.getUpdatedAt();

        // Add a small delay to ensure different timestamps
        TimeUnit.MILLISECONDS.sleep(10);

        transaction.complete();

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertTrue(transaction.getUpdatedAt().after(initialUpdate),
                "Updated date should be after initial date");
    }

    @Test
    void testComplete_FromNonPending() {
        transaction.setStatus(TransactionStatus.IN_PROGRESS);

        Date initialUpdate = transaction.getUpdatedAt();

        transaction.complete();

        // Should remain IN_PROGRESS since complete() only works from PENDING
        assertEquals(TransactionStatus.IN_PROGRESS, transaction.getStatus());

        assertEquals(initialUpdate, transaction.getUpdatedAt());
    }

    @Test
    void testMarkInProgress_FromPending() throws InterruptedException {
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());

        Date initialUpdate = transaction.getUpdatedAt();

        // Add a small delay to ensure different timestamps
        TimeUnit.MILLISECONDS.sleep(10);

        transaction.markInProgress();

        assertEquals(TransactionStatus.IN_PROGRESS, transaction.getStatus());
        assertTrue(transaction.getUpdatedAt().after(initialUpdate),
                "Updated date should be after initial date");
    }

    @Test
    void testMarkInProgress_FromNonPending() {
        transaction.setStatus(TransactionStatus.COMPLETED);

        Date initialUpdate = transaction.getUpdatedAt();

        transaction.markInProgress();

        // Should remain COMPLETED since markInProgress() only works from PENDING
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());

        assertEquals(initialUpdate, transaction.getUpdatedAt());
    }

    @Test
    void testCancel_FromNonCancelled() throws InterruptedException {
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());

        Date initialUpdate = transaction.getUpdatedAt();

        // Add a small delay to ensure different timestamps
        TimeUnit.MILLISECONDS.sleep(10);

        transaction.cancel();

        assertEquals(TransactionStatus.CANCELLED, transaction.getStatus());
        assertTrue(transaction.getUpdatedAt().after(initialUpdate),
                "Updated date should be after initial date");
    }

    @Test
    void testCancel_AlreadyCancelled() throws InterruptedException {
        transaction.setStatus(TransactionStatus.CANCELLED);

        Date initialUpdate = transaction.getUpdatedAt();

        // Add a small delay to ensure different timestamps
        TimeUnit.MILLISECONDS.sleep(10);

        transaction.cancel();

        assertEquals(TransactionStatus.CANCELLED, transaction.getStatus());

        // The cancel method updates the timestamp even if already cancelled
        // based on the implementation: if (this.status != TransactionStatus.CANCELLED)
        // Since it was already cancelled, the updatedAt should remain the same
        assertEquals(initialUpdate, transaction.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Transaction newTransaction = new Transaction();

        newTransaction.setId("new-id");
        assertEquals("new-id", newTransaction.getId());

        newTransaction.setCustomerId("customer-2");
        assertEquals("customer-2", newTransaction.getCustomerId());

        List<TransactionItem> newItems = new ArrayList<>();
        newItems.add(new TransactionItem(product1, 3));
        newTransaction.setItems(newItems);
        assertEquals(1, newTransaction.getItems().size());
        assertEquals(3, newTransaction.getItems().get(0).getQuantity());

        newTransaction.setTotalAmount(500.0);
        assertEquals(500.0, newTransaction.getTotalAmount());

        newTransaction.setPaymentMethod("CARD");
        assertEquals("CARD", newTransaction.getPaymentMethod());

        newTransaction.setStatus(TransactionStatus.COMPLETED);
        assertEquals(TransactionStatus.COMPLETED, newTransaction.getStatus());

        Date newDate = new Date();
        newTransaction.setCreatedAt(newDate);
        assertEquals(newDate, newTransaction.getCreatedAt());

        newTransaction.setUpdatedAt(newDate);
        assertEquals(newDate, newTransaction.getUpdatedAt());

        Payment payment = new Payment();
        payment.setAmount(300.0);
        newTransaction.setPayment(payment);
        assertEquals(300.0, newTransaction.getPayment().getAmount());
    }
}