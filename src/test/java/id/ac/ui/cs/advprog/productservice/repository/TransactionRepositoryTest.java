package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;
    private Product product1;
    private Product product2;
    private Date startDate;
    private Date middleDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        // Create and persist test products
        product1 = new Product("Product 1", "Category 1", 10, 100.0);
        product2 = new Product("Product 2", "Category 2", 20, 200.0);
        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);

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
        transaction1.setTotalAmount(200.0); // 2 * 100.0

        transaction2 = new Transaction();
        transaction2.setId("transaction-2");
        transaction2.setCustomerId("customer-2");
        transaction2.setPaymentMethod("CARD");
        transaction2.setStatus(TransactionStatus.COMPLETED);
        transaction2.setCreatedAt(middleDate);
        transaction2.setUpdatedAt(middleDate);
        transaction2.setTotalAmount(200.0); // 1 * 200.0

        transaction3 = new Transaction();
        transaction3.setId("transaction-3");
        transaction3.setCustomerId("customer-1");
        transaction3.setPaymentMethod("CASH");
        transaction3.setStatus(TransactionStatus.CANCELLED);
        transaction3.setCreatedAt(endDate);
        transaction3.setUpdatedAt(endDate);
        transaction3.setTotalAmount(700.0); // 3 * 100.0 + 2 * 200.0
    }

    @Test
    void save_ShouldSaveNewTransaction() {
        // Act
        Transaction savedTransaction = repository.save(transaction1);

        // Assert
        assertNotNull(savedTransaction);
        assertEquals(transaction1.getId(), savedTransaction.getId());
        assertTrue(repository.existsById(transaction1.getId()));
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
        assertTrue(results.stream().allMatch(t -> "customer-1".equals(t.getCustomerId())));
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
        assertEquals(TransactionStatus.PENDING, results.get(0).getStatus());
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
        assertTrue(results.stream().allMatch(t -> "CASH".equals(t.getPaymentMethod())));
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

        // Act - Find transactions between start and middle (should include first two)
        results = repository.findByDateRange(startDate, middleDate);

        // Assert
        assertEquals(2, results.size());

        // Act - Find transactions between middle and end (should include last two)
        results = repository.findByDateRange(middleDate, endDate);

        // Assert
        assertEquals(2, results.size());
    }

    @Test
    void findByStatusIn_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1); // PENDING
        repository.save(transaction2); // COMPLETED
        repository.save(transaction3); // CANCELLED

        // Act
        List<Transaction> results = repository.findByStatusIn(
                Arrays.asList(TransactionStatus.PENDING, TransactionStatus.COMPLETED)
        );

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t ->
                t.getStatus() == TransactionStatus.PENDING || t.getStatus() == TransactionStatus.COMPLETED
        ));
    }

    @Test
    void findByPaymentMethodIn_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1); // CASH
        repository.save(transaction2); // CARD
        repository.save(transaction3); // CASH

        // Act
        List<Transaction> results = repository.findByPaymentMethodIn(Arrays.asList("CASH", "INSTALLMENT"));

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> "CASH".equals(t.getPaymentMethod())));
    }

    @Test
    void findOngoingTransactions_ShouldReturnPendingAndInProgressTransactions() {
        // Arrange
        transaction1.setStatus(TransactionStatus.PENDING);
        transaction2.setStatus(TransactionStatus.IN_PROGRESS);
        transaction3.setStatus(TransactionStatus.COMPLETED);

        repository.save(transaction1);
        repository.save(transaction2);
        repository.save(transaction3);

        // Act
        List<Transaction> results = repository.findOngoingTransactions();

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t ->
                t.getStatus() == TransactionStatus.PENDING || t.getStatus() == TransactionStatus.IN_PROGRESS
        ));
    }

    @Test
    void searchByKeyword_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1); // ID: transaction-1, Customer: customer-1
        repository.save(transaction2); // ID: transaction-2, Customer: customer-2
        repository.save(transaction3); // ID: transaction-3, Customer: customer-1

        // Act - Search by transaction ID keyword
        List<Transaction> results = repository.searchByKeyword("transaction-1");

        // Assert
        assertEquals(1, results.size());
        assertEquals("transaction-1", results.get(0).getId());

        // Act - Search by customer ID keyword
        results = repository.searchByKeyword("customer-1");

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> "customer-1".equals(t.getCustomerId())));
    }

    @Test
    void findByCustomerIdAndStatus_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1); // customer-1, PENDING
        repository.save(transaction2); // customer-2, COMPLETED
        repository.save(transaction3); // customer-1, CANCELLED

        // Act
        List<Transaction> results = repository.findByCustomerIdAndStatus("customer-1", TransactionStatus.PENDING);

        // Assert
        assertEquals(1, results.size());
        assertEquals("customer-1", results.get(0).getCustomerId());
        assertEquals(TransactionStatus.PENDING, results.get(0).getStatus());
    }

    @Test
    void findByCustomerIdAndDateRange_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1); // customer-1, startDate
        repository.save(transaction2); // customer-2, middleDate
        repository.save(transaction3); // customer-1, endDate

        // Act
        List<Transaction> results = repository.findByCustomerIdAndDateRange("customer-1", startDate, endDate);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> "customer-1".equals(t.getCustomerId())));
    }

    @Test
    void countByStatus_ShouldReturnCorrectCount() {
        // Arrange
        repository.save(transaction1); // PENDING
        repository.save(transaction2); // COMPLETED
        repository.save(transaction3); // CANCELLED

        // Act & Assert
        assertEquals(1, repository.countByStatus(TransactionStatus.PENDING));
        assertEquals(1, repository.countByStatus(TransactionStatus.COMPLETED));
        assertEquals(1, repository.countByStatus(TransactionStatus.CANCELLED));
        assertEquals(0, repository.countByStatus(TransactionStatus.IN_PROGRESS));
    }

    @Test
    void findByCreatedAtAfter_ShouldReturnTransactionsAfterDate() {
        // Arrange
        repository.save(transaction1); // startDate
        repository.save(transaction2); // middleDate
        repository.save(transaction3); // endDate

        // Act
        List<Transaction> results = repository.findByCreatedAtAfter(middleDate);

        // Assert
        assertEquals(1, results.size());
        assertEquals(transaction3.getId(), results.get(0).getId());
    }

    @Test
    void findByCreatedAtBefore_ShouldReturnTransactionsBeforeDate() {
        // Arrange
        repository.save(transaction1); // startDate
        repository.save(transaction2); // middleDate
        repository.save(transaction3); // endDate

        // Act
        List<Transaction> results = repository.findByCreatedAtBefore(middleDate);

        // Assert
        assertEquals(1, results.size());
        assertEquals(transaction1.getId(), results.get(0).getId());
    }

    @Test
    void findByTotalAmountGreaterThan_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1); // 200.0
        repository.save(transaction2); // 200.0
        repository.save(transaction3); // 700.0

        // Act
        List<Transaction> results = repository.findByTotalAmountGreaterThan(300.0);

        // Assert
        assertEquals(1, results.size());
        assertEquals(transaction3.getId(), results.get(0).getId());
        assertTrue(results.get(0).getTotalAmount() > 300.0);
    }

    @Test
    void findByTotalAmountLessThan_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1); // 200.0
        repository.save(transaction2); // 200.0
        repository.save(transaction3); // 700.0

        // Act
        List<Transaction> results = repository.findByTotalAmountLessThan(300.0);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getTotalAmount() < 300.0));
    }

    @Test
    void findByTotalAmountBetween_ShouldReturnTransactionsInRange() {
        // Arrange
        repository.save(transaction1); // 200.0
        repository.save(transaction2); // 200.0
        repository.save(transaction3); // 700.0

        // Act
        List<Transaction> results = repository.findByTotalAmountBetween(150.0, 250.0);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(t -> t.getTotalAmount() >= 150.0 && t.getTotalAmount() <= 250.0));
    }

    @Test
    void findTransactionsWithFilters_ShouldReturnMatchingTransactions() {
        // Arrange
        repository.save(transaction1); // customer-1, PENDING, CASH, startDate, 200.0
        repository.save(transaction2); // customer-2, COMPLETED, CARD, middleDate, 200.0
        repository.save(transaction3); // customer-1, CANCELLED, CASH, endDate, 700.0

        // Act - Filter by customer ID only
        List<Transaction> results = repository.findTransactionsWithFilters(
                "customer-1", null, null, null, null
        );

        // Assert
        assertEquals(2, results.size());

        // Act - Filter by status and payment method
        results = repository.findTransactionsWithFilters(
                null,
                Arrays.asList(TransactionStatus.PENDING, TransactionStatus.COMPLETED),
                Arrays.asList("CASH", "CARD"),
                null,
                null
        );

        // Assert
        assertEquals(2, results.size());

        // Act - Filter by date range
        results = repository.findTransactionsWithFilters(
                null, null, null, startDate, middleDate
        );

        // Assert
        assertEquals(2, results.size());
    }

    @Test
    void deleteById_ShouldRemoveTransaction() {
        // Arrange
        repository.save(transaction1);
        repository.save(transaction2);
        assertTrue(repository.existsById(transaction1.getId()));

        // Act
        repository.deleteById(transaction1.getId());

        // Assert
        assertFalse(repository.existsById(transaction1.getId()));
        assertEquals(1, repository.findAll().size());
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
}