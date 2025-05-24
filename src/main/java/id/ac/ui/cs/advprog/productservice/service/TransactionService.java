package id.ac.ui.cs.advprog.productservice.service;

import id.ac.ui.cs.advprog.productservice.dto.TransactionDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionRequestDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionUpdateDTO;
import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {
    TransactionDTO createTransaction(TransactionRequestDTO requestDTO);
//    TransactionDTO createDraftTransaction(TransactionRequestDTO requestDTO);
    TransactionDTO getTransactionById(String id);
    List<TransactionDTO> getAllTransactions();
    List<TransactionDTO> getTransactionsByCustomerId(String customerId);
    List<TransactionDTO> getTransactionsByStatus(TransactionStatus status);
    List<TransactionDTO> getTransactionsByPaymentMethod(String paymentMethod);
    List<TransactionDTO> getTransactionsByDateRange(Date startDate, Date endDate);
    TransactionDTO updateTransaction(String id, TransactionUpdateDTO updateDTO);
    TransactionDTO completeTransaction(String id);
    TransactionDTO cancelTransaction(String id);
    void deleteTransaction(String id);
    List<TransactionDTO> searchTransactions(String keyword);
    List<TransactionDTO> getOngoingTransactions();
    List<TransactionDTO> filterTransactions(String customerId, List<TransactionStatus> statuses, List<String> paymentMethods, Date startDate, Date endDate, String sortBy, String sortDirection);
    TransactionDTO confirmTransaction(String id);
    CompletableFuture<Map<String, Object>> getTransactionDetails(String id);
    int batchCompleteTransactions(List<String> transactionIds);
    int batchCancelTransactions(List<String> transactionIds);

    @Async
    CompletableFuture<Integer> batchCompleteTransactionsAsync(List<String> transactionIds);

    @Async
    CompletableFuture<Integer> batchCancelTransactionsAsync(List<String> transactionIds);

}