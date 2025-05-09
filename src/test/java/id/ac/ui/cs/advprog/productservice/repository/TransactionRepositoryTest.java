package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.model.TransactionItem;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest {

    private TransactionRepository repository;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;
    private Date startDate;
    private Date middleDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepository();

        // Create test products
        Product product1 = new Product("Product 1", "Category 1", 10, 100.0);
        Product product2 = new Product("Product 2", "Category 2", 20, 200.0);

        // Create dates for testing
        Calendar calendar = Calendar.getInstance();

        // End date (current date)
        calendar.set(Calendar.MILLISECOND, 0); // Clear milliseconds for easier comparison
        endDate = calendar.getTime();

        // Start date (30 days ago)
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        startDate = calendar.getTime();

        // Middle date (15 days ago)
        calendar.add(Calendar.DAY_OF_MONTH, 15);
        middleDate = calendar.getTime();

        // Create transactions with different properties for testing
        transaction1 = new Transaction();
        transaction1.setId("transaction-1");
        transaction1.setCustomerId("customer-1");
        transaction1.setPaymentMethod("CASH");
        transaction1.setStatus(TransactionStatus.PENDING);
        transaction1.setCreatedAt(startDate);
        transaction1.setUpdatedAt(startDate);
        transaction1.addItem(new TransactionItem(product1, 2));

        transaction2 = new Transaction();
        transaction2.setId("transaction-2");
        transaction2.setCustomerId("customer-2");
        transaction2.setPaymentMethod("CARD");
        transaction2.setStatus(TransactionStatus.COMPLETED);
        transaction2.setCreatedAt(middleDate);
        transaction2.setUpdatedAt(middleDate);
        transaction2.addItem(new TransactionItem(product2, 1));

        transaction3 = new Transaction();
        transaction3.setId("transaction-3");
        transaction3.setCustomerId("customer-1");
        transaction3.setPaymentMethod("CASH");
        transaction3.setStatus(TransactionStatus.CANCELLED);
        transaction3.setCreatedAt(endDate);
        transaction3.setUpdatedAt(endDate);
        transaction3.addItem(new TransactionItem(product1, 3));
        transaction3.addItem(new TransactionItem(product2, 2));
    }

    @Test
    void save_ShouldSaveNewTransaction() {
        // Act
        Transaction savedTransaction = repository.save(transaction1);

        // Assert
        assertEquals(transaction1, savedTransaction);
        assertTrue(repository.existsById(transaction1.getId()));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void save_ShouldUpdateExistingTransaction() {
        // Arrange
        repository.save(transaction1);
        transaction1.setPaymentMethod("UPDATED_PAYMENT_METHOD");

        // Act
        Transaction updatedTransaction = repository.save(transaction1);

        // Assert
        assertEquals("UPDATED_PAYMENT_METHOD", updatedTransaction.getPaymentMethod());
        Optional<Transaction> retrievedTransaction = repository.findById(transaction1.getId());
        assertTrue(retrievedTransaction.isPresent());
        assertEquals("UPDATED_PAYMENT_METHOD", retrievedTransaction.get().getPaymentMethod());
    }

    @Test
    void findById_ShouldReturnTransaction_WhenExists() {
        // Arrange
        repository.save(transaction1);

        // Act
        Optional<Transaction> result = repository.findById(transaction1.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(transaction1.getId(), result.get().getId());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Act
        Optional<Transaction> result = repository.findById("non-existent-id");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllTransactions() {
        // Arrange
        repository.save(transaction1);
        repository.save(transaction2);
        repository.save(transaction3);

        // Act
        List<Transaction> results = repository.findAll();

        // Assert
        assertEquals(3, results.size());
        assertTrue(results.contains(transaction1));
        assertTrue(results.contains(transaction2));
        assertTrue(results.contains(transaction3));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoTransactions() {
        // Act
        List<Transaction> results = repository.findAll();

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void findByCustomerId_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1);
        repository.save(transaction2);
        repository.save(transaction3);

        // Act
        List<Transaction> results = repository.findByCustomerId("customer-1");

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.contains(transaction1));
        assertTrue(results.contains(transaction3));
    }

    @Test
    void findByCustomerId_ShouldReturnEmptyList_WhenNoMatches() {
        // Arrange
        repository.save(transaction1);
        repository.save(transaction2);

        // Act
        List<Transaction> results = repository.findByCustomerId("non-existent-customer");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void findByStatus_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1);
        repository.save(transaction2);
        repository.save(transaction3);

        // Act
        List<Transaction> results = repository.findByStatus(TransactionStatus.PENDING);

        // Assert
        assertEquals(1, results.size());
        assertEquals(transaction1, results.get(0));
    }

    @Test
    void findByStatus_ShouldReturnEmptyList_WhenNoMatches() {
        // Arrange
        repository.save(transaction1); // PENDING
        repository.save(transaction2); // COMPLETED

        // Act
        List<Transaction> results = repository.findByStatus(TransactionStatus.IN_PROGRESS);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void findByPaymentMethod_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1);
        repository.save(transaction2);
        repository.save(transaction3);

        // Act
        List<Transaction> results = repository.findByPaymentMethod("CASH");

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.contains(transaction1));
        assertTrue(results.contains(transaction3));
    }

    @Test
    void findByPaymentMethod_ShouldReturnEmptyList_WhenNoMatches() {
        // Arrange
        repository.save(transaction1); // CASH
        repository.save(transaction2); // CARD

        // Act
        List<Transaction> results = repository.findByPaymentMethod("INSTALLMENT");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void findByDateRange_ShouldReturnTransactionsInRange() {
        // Arrange
        repository.save(transaction1); // startDate
        repository.save(transaction2); // middleDate
        repository.save(transaction3); // endDate

        // Act - Find transactions between start and end (should include all)
        List<Transaction> results = repository.findByDateRange(startDate, endDate);

        // Assert
        assertEquals(3, results.size());
        assertTrue(results.contains(transaction1));
        assertTrue(results.contains(transaction2));
        assertTrue(results.contains(transaction3));

        // Act - Find transactions between start and middle (should include first two)
        results = repository.findByDateRange(startDate, middleDate);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.contains(transaction1));
        assertTrue(results.contains(transaction2));

        // Act - Find transactions between middle and end (should include last two)
        results = repository.findByDateRange(middleDate, endDate);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.contains(transaction2));
        assertTrue(results.contains(transaction3));
    }

    @Test
    void findByDateRange_ShouldReturnEmptyList_WhenNoTransactionsInRange() {
        // Arrange
        repository.save(transaction2); // middleDate

        // Create a date before startDate
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        Date beforeStartDate = calendar.getTime();

        // Create a date after endDate
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        Date afterEndDate = calendar.getTime();

        // Act - Find transactions before any exist
        List<Transaction> results = repository.findByDateRange(beforeStartDate, startDate);

        // Assert
        assertTrue(results.isEmpty());

        // Act - Find transactions after any exist
        results = repository.findByDateRange(endDate, afterEndDate);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void delete_ShouldRemoveTransaction() {
        // Arrange
        repository.save(transaction1);
        repository.save(transaction2);
        assertTrue(repository.existsById(transaction1.getId()));

        // Act
        repository.delete(transaction1.getId());

        // Assert
        assertFalse(repository.existsById(transaction1.getId()));
        assertEquals(1, repository.findAll().size());
        assertTrue(repository.findAll().contains(transaction2));
    }

    @Test
    void delete_ShouldDoNothing_WhenTransactionDoesNotExist() {
        // Arrange
        repository.save(transaction1);

        // Act - Should not throw an exception
        repository.delete("non-existent-id");

        // Assert - Original transaction should still exist
        assertEquals(1, repository.findAll().size());
        assertTrue(repository.existsById(transaction1.getId()));
    }

    @Test
    void existsById_ShouldReturnTrue_WhenTransactionExists() {
        // Arrange
        repository.save(transaction1);

        // Act & Assert
        assertTrue(repository.existsById(transaction1.getId()));
    }

    @Test
    void existsById_ShouldReturnFalse_WhenTransactionDoesNotExist() {
        // Act & Assert
        assertFalse(repository.existsById("non-existent-id"));
    }

    @Test
    void concurrentOperations_ShouldWorkCorrectly() {
        // This is a basic test to check that ConcurrentHashMap is being used properly

        // Arrange - Save initial transactions
        repository.save(transaction1);
        repository.save(transaction2);

        // Act - Simulate concurrent operations by interleaving operations
        // Thread 1: Find all transactions
        List<Transaction> allTransactions = repository.findAll();

        // Thread 2: Add a new transaction
        repository.save(transaction3);

        // Thread 1: Find by customer ID
        List<Transaction> customerTransactions = repository.findByCustomerId("customer-1");

        // Thread 2: Update transaction1
        transaction1.setStatus(TransactionStatus.COMPLETED);
        repository.save(transaction1);

        // Thread 1: Find by ID
        Optional<Transaction> foundTransaction = repository.findById(transaction1.getId());

        // Assert - The repository should maintain consistent state
        assertEquals(3, repository.findAll().size());
        assertEquals(2, customerTransactions.size());
        assertTrue(foundTransaction.isPresent());
        assertEquals(TransactionStatus.COMPLETED, foundTransaction.get().getStatus());
    }

    @Test
    void repositoryState_ShouldBeIndependentOfReturnedCollections() {
        // Arrange
        repository.save(transaction1);
        repository.save(transaction2);

        // Act - Get a list of all transactions and modify it
        List<Transaction> transactions = repository.findAll();
        transactions.clear(); // This shouldn't affect the repository

        // Assert
        assertEquals(2, repository.findAll().size());
        assertTrue(repository.existsById(transaction1.getId()));
        assertTrue(repository.existsById(transaction2.getId()));
    }
}