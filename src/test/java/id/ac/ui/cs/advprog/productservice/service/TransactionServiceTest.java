package id.ac.ui.cs.advprog.productservice.service;

import id.ac.ui.cs.advprog.productservice.dto.TransactionDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionRequestDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionUpdateDTO;
import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.model.TransactionItem;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import id.ac.ui.cs.advprog.productservice.repository.PaymentRepository;
import id.ac.ui.cs.advprog.productservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private Executor customTaskExecutor;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Product product1;
    private Product product2;
    private Transaction transaction;
    private Payment payment;
    private TransactionRequestDTO requestDTO;
    private TransactionUpdateDTO updateDTO;
    private String transactionId;
    private String customerId;
    private List<TransactionItem> transactionItems;

    @BeforeEach
    void setUp() {
        product1 = spy(new Product("Product 1", "Category 1", 10, 100.0));
        product2 = spy(new Product("Product 2", "Category 2", 20, 200.0));

        when(product1.getId()).thenReturn(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"));
        when(product2.getId()).thenReturn(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));

        transactionId = "test-transaction-id";
        customerId = "test-customer-id";

        // Create payment
        payment = new Payment("payment-id", customerId, 200.0, "CASH", "LUNAS", new Date());

        transactionItems = new ArrayList<>();
        TransactionItem item1 = new TransactionItem(product1, 2);
        transactionItems.add(item1);

        transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setCustomerId(customerId);
        transaction.setPaymentMethod("CASH");
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(new Date());
        transaction.setUpdatedAt(new Date());
        transaction.setPayment(payment);

        for (TransactionItem item : transactionItems) {
            transaction.addItem(item);
        }
        transaction.calculateTotalAmount();

        when(productService.getProductById("550e8400-e29b-41d4-a716-446655440001")).thenReturn(Optional.of(product1));
        when(productService.getProductById("550e8400-e29b-41d4-a716-446655440002")).thenReturn(Optional.of(product2));

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            if (p.getId() == null) {
                p.setId("generated-payment-id");
            }
            return p;
        });

        List<Transaction> transactionList = Collections.singletonList(transaction);
        when(transactionRepository.findAll()).thenReturn(transactionList);
        when(transactionRepository.findByCustomerId(customerId)).thenReturn(transactionList);
        when(transactionRepository.findByStatus(TransactionStatus.PENDING)).thenReturn(transactionList);
        when(transactionRepository.findByPaymentMethod("CASH")).thenReturn(transactionList);
        when(transactionRepository.findByDateRange(any(Date.class), any(Date.class))).thenReturn(transactionList);
        when(transactionRepository.searchByKeyword("test")).thenReturn(transactionList);
        when(transactionRepository.findOngoingTransactions()).thenReturn(transactionList);
        when(transactionRepository.findTransactionsWithFilters(any(), any(), any(), any(), any())).thenReturn(transactionList);

        requestDTO = new TransactionRequestDTO();
        requestDTO.setCustomerId(customerId);
        requestDTO.setPaymentMethod("CASH");
        requestDTO.setAmount(200.0); // Full payment amount

        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("550e8400-e29b-41d4-a716-446655440001", 2);
        requestDTO.setProductQuantities(productQuantities);

        updateDTO = new TransactionUpdateDTO();
        updateDTO.setCustomerId("updated-customer-id");
        updateDTO.setPaymentMethod("CARD");

        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return null;
        }).when(customTaskExecutor)
                .execute(any(Runnable.class));
    }

    @Test
    void createTransaction_FullPayment_Success() {
        requestDTO.setAmount(200.0); // Full payment

        TransactionDTO result = transactionService.createTransaction(requestDTO);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals("CASH", result.getPaymentMethod());
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());

        verify(productService, times(1)).getProductById("550e8400-e29b-41d4-a716-446655440001");
        verify(productService, times(1)).editProduct(any(Product.class), eq(true));
        verify(paymentRepository).save(any(Payment.class));
        verify(transactionRepository).save(any(Transaction.class));

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals(1, savedTransaction.getItems().size());
        assertNotNull(savedTransaction.getPayment());
    }

    @Test
    void createTransaction_PartialPayment_InProgress() {
        requestDTO.setAmount(100.0); // Partial payment

        TransactionDTO result = transactionService.createTransaction(requestDTO);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals("CASH", result.getPaymentMethod());
        assertEquals(TransactionStatus.IN_PROGRESS, result.getStatus());

        verify(paymentRepository).save(any(Payment.class));
        verify(transactionRepository).save(any(Transaction.class));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertEquals("CICILAN", savedPayment.getStatus());
        assertEquals(100.0, savedPayment.getAmount());
    }

    @Test
    void createTransaction_ProductNotFound() {
        when(productService.getProductById("non-existent-product")).thenReturn(Optional.empty());
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("non-existent-product", 1);
        requestDTO.setProductQuantities(productQuantities);
        requestDTO.setAmount(100.0);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            transactionService.createTransaction(requestDTO);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void createTransaction_InsufficientStock() {
        when(product1.getStock()).thenReturn(1);

        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("550e8400-e29b-41d4-a716-446655440001", 2);
        requestDTO.setProductQuantities(productQuantities);
        requestDTO.setAmount(200.0);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            transactionService.createTransaction(requestDTO);
        });

        assertTrue(exception.getMessage().contains("Not enough stock"));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void getTransactionById_Success() {
        TransactionDTO result = transactionService.getTransactionById(transactionId);

        assertNotNull(result);
        assertEquals(transactionId, result.getId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(1, result.getItems().size());
        assertEquals(TransactionStatus.PENDING, result.getStatus());

        verify(transactionRepository).findById(transactionId);
    }

    @Test
    void getTransactionById_NotFound() {
        when(transactionRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            transactionService.getTransactionById("non-existent-id");
        });

        assertTrue(exception.getMessage().contains("Transaction not found"));
        verify(transactionRepository).findById("non-existent-id");
    }

    @Test
    void getAllTransactions_Success() {
        List<TransactionDTO> results = transactionService.getAllTransactions();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(transactionId, results.get(0).getId());

        verify(transactionRepository).findAll();
    }

    @Test
    void getTransactionsByCustomerId_Success() {
        List<TransactionDTO> results = transactionService.getTransactionsByCustomerId(customerId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(customerId, results.get(0).getCustomerId());

        verify(transactionRepository).findByCustomerId(customerId);
    }

    @Test
    void getTransactionsByStatus_Success() {
        List<TransactionDTO> results = transactionService.getTransactionsByStatus(TransactionStatus.PENDING);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(TransactionStatus.PENDING, results.get(0).getStatus());

        verify(transactionRepository).findByStatus(TransactionStatus.PENDING);
    }

    @Test
    void getTransactionsByPaymentMethod_Success() {
        List<TransactionDTO> results = transactionService.getTransactionsByPaymentMethod("CASH");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("CASH", results.get(0).getPaymentMethod());

        verify(transactionRepository).findByPaymentMethod("CASH");
    }

    @Test
    void getTransactionsByDateRange_Success() {
        Date startDate = new Date(System.currentTimeMillis() - 86400000);
        Date endDate = new Date();

        List<TransactionDTO> results = transactionService.getTransactionsByDateRange(startDate, endDate);

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(transactionRepository).findByDateRange(startDate, endDate);
    }

    @Test
    void updateTransaction_BasicFields_Success() {
        TransactionDTO result = transactionService.updateTransaction(transactionId, updateDTO);

        assertNotNull(result);

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).save(any(Transaction.class));

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals("updated-customer-id", savedTransaction.getCustomerId());
        assertEquals("CARD", savedTransaction.getPaymentMethod());
    }

    @Test
    void updateTransaction_PaymentAmount_Success() {
        // Set up transaction with installment payment
        payment.setStatus("CICILAN");
        payment.setAmount(100.0);
        transaction.setStatus(TransactionStatus.IN_PROGRESS);

        updateDTO = new TransactionUpdateDTO();
        updateDTO.setAmount(200.0); // Full payment

        TransactionDTO result = transactionService.updateTransaction(transactionId, updateDTO);

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals("LUNAS", savedTransaction.getPayment().getStatus());
        assertEquals(200.0, savedTransaction.getPayment().getAmount());
    }

    @Test
    void updateTransaction_NotFound() {
        when(transactionRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            transactionService.updateTransaction("non-existent-id", updateDTO);
        });

        assertTrue(exception.getMessage().contains("Transaction not found"));
    }

    @Test
    void updateTransaction_InvalidStatus() {
        transaction.setStatus(TransactionStatus.COMPLETED);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            transactionService.updateTransaction(transactionId, updateDTO);
        });

        assertTrue(exception.getMessage().contains("Cannot update transaction with status"));
    }

    @Test
    void completeTransaction_Success() {
        TransactionDTO result = transactionService.completeTransaction(transactionId);

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void completeTransaction_NotFound() {
        when(transactionRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            transactionService.completeTransaction("non-existent-id");
        });

        assertTrue(exception.getMessage().contains("Transaction not found"));
    }

    @Test
    void completeTransaction_InvalidStatus() {
        transaction.setStatus(TransactionStatus.COMPLETED);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            transactionService.completeTransaction(transactionId);
        });

        assertTrue(exception.getMessage().contains("Cannot complete transaction with status"));
    }

    @Test
    void cancelTransaction_Success() {
        TransactionDTO result = transactionService.cancelTransaction(transactionId);

        assertNotNull(result);
        assertEquals(TransactionStatus.CANCELLED, result.getStatus());

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).save(any(Transaction.class));
        verify(productService, atLeastOnce()).editProduct(any(Product.class), eq(true));
    }

    @Test
    void cancelTransaction_NotFound() {
        when(transactionRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            transactionService.cancelTransaction("non-existent-id");
        });

        assertTrue(exception.getMessage().contains("Transaction not found"));
    }

    @Test
    void cancelTransaction_AlreadyCancelled() {
        transaction.setStatus(TransactionStatus.CANCELLED);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            transactionService.cancelTransaction(transactionId);
        });

        assertTrue(exception.getMessage().contains("Transaction is already cancelled"));
    }

    @Test
    void cancelTransaction_AlreadyCompleted() {
        transaction.setStatus(TransactionStatus.COMPLETED);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            transactionService.cancelTransaction(transactionId);
        });

        assertTrue(exception.getMessage().contains("Transaction is already completed"));
    }

    @Test
    void deleteTransaction_Success() {
        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).deleteById(transactionId);
        verify(productService, atLeastOnce()).editProduct(any(Product.class), eq(true));
    }

    @Test
    void deleteTransaction_NotFound() {
        when(transactionRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            transactionService.deleteTransaction("non-existent-id");
        });

        assertTrue(exception.getMessage().contains("Transaction not found"));
    }

    @Test
    void deleteTransaction_AlreadyCancelled() {
        transaction.setStatus(TransactionStatus.CANCELLED);

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).deleteById(transactionId);
        verify(productService, never()).editProduct(any(Product.class), eq(true));
    }

    @Test
    void searchTransactions_WithKeyword() {
        List<TransactionDTO> results = transactionService.searchTransactions("test");

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(transactionRepository).searchByKeyword("test");
    }

    @Test
    void searchTransactions_EmptyKeyword() {
        List<TransactionDTO> results = transactionService.searchTransactions("");

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(transactionRepository).findAll();
    }

    @Test
    void getOngoingTransactions_Success() {
        List<Transaction> allTransactions = new ArrayList<>();

        Transaction pendingTransaction = new Transaction();
        pendingTransaction.setId("pending-id");
        pendingTransaction.setStatus(TransactionStatus.PENDING);
        // Add payment to pendingTransaction
        Payment pendingPayment = new Payment("pending-payment-id", "customer-1", 100.0, "CASH", "CICILAN", new Date());
        pendingTransaction.setPayment(pendingPayment);

        Transaction inProgressTransaction = new Transaction();
        inProgressTransaction.setId("in-progress-id");
        inProgressTransaction.setStatus(TransactionStatus.IN_PROGRESS);
        // Add payment to inProgressTransaction
        Payment inProgressPayment = new Payment("in-progress-payment-id", "customer-2", 150.0, "CARD", "CICILAN", new Date());
        inProgressTransaction.setPayment(inProgressPayment);

        allTransactions.add(pendingTransaction);
        allTransactions.add(inProgressTransaction);

        when(transactionRepository.findOngoingTransactions()).thenReturn(allTransactions);

        List<TransactionDTO> results = transactionService.getOngoingTransactions();

        assertNotNull(results);
        assertEquals(2, results.size());

        Set<String> includedIds = new HashSet<>();
        for (TransactionDTO dto : results) {
            includedIds.add(dto.getId());
        }
        assertTrue(includedIds.contains("pending-id"));
        assertTrue(includedIds.contains("in-progress-id"));
    }

    @Test
    void filterTransactions_AllFilters() {
        List<Transaction> testTransactions = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.JANUARY, 15);
        Date middleDate = calendar.getTime();

        calendar.set(2023, Calendar.JANUARY, 1);
        Date startDate = calendar.getTime();

        calendar.set(2023, Calendar.JANUARY, 31);
        Date endDate = calendar.getTime();

        Transaction matchingTransaction = new Transaction();
        matchingTransaction.setId("matching-transaction");
        matchingTransaction.setCustomerId("customer-1");
        matchingTransaction.setPaymentMethod("CASH");
        matchingTransaction.setStatus(TransactionStatus.PENDING);
        matchingTransaction.setCreatedAt(middleDate);
        // Add payment to matchingTransaction
        Payment matchingPayment = new Payment("matching-payment-id", "customer-1", 200.0, "CASH", "LUNAS", middleDate);
        matchingTransaction.setPayment(matchingPayment);

        testTransactions.add(matchingTransaction);

        when(transactionRepository.findTransactionsWithFilters(
                eq("customer-1"),
                eq(Collections.singletonList(TransactionStatus.PENDING)),
                eq(Collections.singletonList("CASH")),
                eq(startDate),
                eq(endDate)
        )).thenReturn(testTransactions);

        List<TransactionDTO> results = transactionService.filterTransactions(
                "customer-1",
                Collections.singletonList(TransactionStatus.PENDING),
                Collections.singletonList("CASH"),
                startDate,
                endDate,
                "createdAt",
                "desc"
        );

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("matching-transaction", results.get(0).getId());
    }

    @Test
    void confirmTransaction_Success() {
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());

        TransactionDTO result = transactionService.confirmTransaction(transactionId);

        assertNotNull(result);
        assertEquals(TransactionStatus.IN_PROGRESS, result.getStatus());

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void confirmTransaction_NotFound() {
        when(transactionRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            transactionService.confirmTransaction("non-existent-id");
        });

        assertTrue(exception.getMessage().contains("Transaction not found"));
    }

    @Test
    void confirmTransaction_InvalidStatus() {
        transaction.setStatus(TransactionStatus.COMPLETED);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            transactionService.confirmTransaction(transactionId);
        });

        assertTrue(exception.getMessage().contains("Only pending transactions can be confirmed"));
    }

    @Test
    void confirmTransaction_InsufficientStock() {
        Product lowStockProduct = spy(new Product("Low Stock Product", "Category", 1, 100.0));
        when(lowStockProduct.getId()).thenReturn(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"));
        when(lowStockProduct.getStock()).thenReturn(1);

        TransactionItem insufficientItem = new TransactionItem(lowStockProduct, 5);
        transaction.addItem(insufficientItem);

        when(productService.getProductById("550e8400-e29b-41d4-a716-446655440003")).thenReturn(Optional.of(lowStockProduct));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            transactionService.confirmTransaction(transactionId);
        });

        assertTrue(exception.getMessage().contains("Not enough stock"));
    }

    @Test
    void getTransactionDetails_Success() throws Exception {
        CompletableFuture<Map<String, Object>> future = transactionService.getTransactionDetails(transactionId);
        Map<String, Object> details = future.get();

        assertNotNull(details);
        assertTrue(details.containsKey("transaction"));
        assertTrue(details.containsKey("stockStatus"));

        TransactionDTO transactionDTO = (TransactionDTO) details.get("transaction");
        assertEquals(transactionId, transactionDTO.getId());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> stockStatus = (List<Map<String, Object>>) details.get("stockStatus");
        assertFalse(stockStatus.isEmpty());

        Map<String, Object> productStatus = stockStatus.get(0);
        assertEquals(product1.getId().toString(), productStatus.get("productId"));
        assertEquals(product1.getName(), productStatus.get("productName"));
        assertEquals(2, productStatus.get("quantityInTransaction"));

        verify(transactionRepository).findById(transactionId);
    }

    @Test
    void getTransactionDetails_NotFound() throws Exception {
        when(transactionRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        CompletableFuture<Map<String, Object>> future = transactionService.getTransactionDetails("non-existent-id");

        ExecutionException executionException = assertThrows(ExecutionException.class, future::get);
        Throwable cause = executionException.getCause();
        assertInstanceOf(NoSuchElementException.class, cause);
        assertTrue(cause.getMessage().contains("Transaction not found"));
    }

    @Test
    void batchCompleteTransactions_Success() {
        Transaction pendingTransaction = new Transaction();
        pendingTransaction.setId("pending-id");
        pendingTransaction.setStatus(TransactionStatus.PENDING);

        Transaction completedTransaction = new Transaction();
        completedTransaction.setId("completed-id");
        completedTransaction.setStatus(TransactionStatus.COMPLETED);

        when(transactionRepository.findById("pending-id")).thenReturn(Optional.of(pendingTransaction));
        when(transactionRepository.findById("completed-id")).thenReturn(Optional.of(completedTransaction));

        List<String> transactionIds = Arrays.asList("pending-id", "completed-id");

        int completedCount = transactionService.batchCompleteTransactions(transactionIds);

        assertEquals(1, completedCount);

        verify(transactionRepository).findById("pending-id");
        verify(transactionRepository).findById("completed-id");

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals("pending-id", savedTransaction.getId());
        assertEquals(TransactionStatus.COMPLETED, savedTransaction.getStatus());
    }

    @Test
    void batchCancelTransactions_Success() {
        Transaction pendingTransaction = new Transaction();
        pendingTransaction.setId("pending-id");
        pendingTransaction.setStatus(TransactionStatus.PENDING);

        TransactionItem item = new TransactionItem(product1, 2);
        pendingTransaction.addItem(item);

        Transaction cancelledTransaction = new Transaction();
        cancelledTransaction.setId("cancelled-id");
        cancelledTransaction.setStatus(TransactionStatus.CANCELLED);

        when(transactionRepository.findById("pending-id")).thenReturn(Optional.of(pendingTransaction));
        when(transactionRepository.findById("cancelled-id")).thenReturn(Optional.of(cancelledTransaction));

        List<String> transactionIds = Arrays.asList("pending-id", "cancelled-id");

        int cancelledCount = transactionService.batchCancelTransactions(transactionIds);

        assertEquals(1, cancelledCount);

        verify(transactionRepository).findById("pending-id");
        verify(transactionRepository).findById("cancelled-id");

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals("pending-id", savedTransaction.getId());
        assertEquals(TransactionStatus.CANCELLED, savedTransaction.getStatus());

        verify(productService, atLeastOnce()).editProduct(any(Product.class), eq(true));
    }

    @Test
    void batchOperations_ErrorHandling() {
        List<String> transactionIds = Collections.singletonList("error-id");

        when(transactionRepository.findById("error-id")).thenThrow(new RuntimeException("Test exception"));

        int completedCount = transactionService.batchCompleteTransactions(transactionIds);
        int cancelledCount = transactionService.batchCancelTransactions(transactionIds);

        assertEquals(0, completedCount);
        assertEquals(0, cancelledCount);

        verify(transactionRepository, times(2)).findById("error-id");
    }
}