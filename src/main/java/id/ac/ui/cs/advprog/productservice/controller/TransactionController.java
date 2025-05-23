package id.ac.ui.cs.advprog.productservice.controller;

import id.ac.ui.cs.advprog.productservice.dto.TransactionDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionRequestDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionUpdateDTO;
import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionRequestDTO requestDTO) {
        TransactionDTO transaction = transactionService.createTransaction(requestDTO);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable String id) {
        TransactionDTO transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {

        List<TransactionDTO> transactions;

        if (customerId != null) {
            transactions = transactionService.getTransactionsByCustomerId(customerId);
        } else if (status != null) {
            transactions = transactionService.getTransactionsByStatus(status);
        } else if (paymentMethod != null) {
            transactions = transactionService.getTransactionsByPaymentMethod(paymentMethod);
        } else if (startDate != null && endDate != null) {
            transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        } else {
            transactions = transactionService.getAllTransactions();
        }

        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable String id,
            @RequestBody TransactionUpdateDTO updateDTO) {

        TransactionDTO transaction = transactionService.updateTransaction(id, updateDTO);
        return ResponseEntity.ok(transaction);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TransactionDTO> completeTransaction(@PathVariable String id) {
        TransactionDTO transaction = transactionService.completeTransaction(id);
        return ResponseEntity.ok(transaction);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TransactionDTO> cancelTransaction(@PathVariable String id) {
        TransactionDTO transaction = transactionService.cancelTransaction(id);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionDTO>> searchTransactions(
            @RequestParam(required = false) String keyword) {

        List<TransactionDTO> transactions = transactionService.searchTransactions(keyword);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<TransactionDTO>> getOngoingTransactions() {
        List<TransactionDTO> transactions = transactionService.getOngoingTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TransactionDTO>> getPendingTransactions() {
        List<TransactionDTO> transactions = transactionService.getTransactionsByStatus(TransactionStatus.PENDING);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<TransactionDTO>> getInProgressTransactions() {
        List<TransactionDTO> transactions = transactionService.getTransactionsByStatus(TransactionStatus.IN_PROGRESS);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionDTO>> filterTransactions(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) List<TransactionStatus> statuses,
            @RequestParam(required = false) List<String> paymentMethods,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {

        List<TransactionDTO> transactions = transactionService.filterTransactions(
                customerId, statuses, paymentMethods, startDate, endDate, sortBy, sortDirection);

        return ResponseEntity.ok(transactions);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<TransactionDTO> confirmTransaction(@PathVariable String id) {
        TransactionDTO transaction = transactionService.confirmTransaction(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{id}/details")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getTransactionDetails(@PathVariable String id) {
        return transactionService.getTransactionDetails(id)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/batch/complete")
    public ResponseEntity<Integer> completeMultipleTransactions(@RequestBody List<String> transactionIds) {
        int completedCount = transactionService.batchCompleteTransactions(transactionIds);
        return ResponseEntity.ok(completedCount);
    }

    @PostMapping("/batch/cancel")
    public ResponseEntity<Integer> cancelMultipleTransactions(@RequestBody List<String> transactionIds) {
        int canceledCount = transactionService.batchCancelTransactions(transactionIds);
        return ResponseEntity.ok(canceledCount);
    }

    @PostMapping("/batch/complete/async")
    public CompletableFuture<ResponseEntity<Integer>> completeMultipleTransactionsAsync(
            @RequestBody List<String> transactionIds) {
        return transactionService.batchCompleteTransactionsAsync(transactionIds)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/batch/cancel/async")
    public CompletableFuture<ResponseEntity<Integer>> cancelMultipleTransactionsAsync(
            @RequestBody List<String> transactionIds) {
        return transactionService.batchCancelTransactionsAsync(transactionIds)
                .thenApply(ResponseEntity::ok);
    }
}