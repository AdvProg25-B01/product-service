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

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {

    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionRequestDTO requestDTO) {

    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable String id) {

    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(

    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(

    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TransactionDTO> completeTransaction(@PathVariable String id) {

    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TransactionDTO> cancelTransaction(@PathVariable String id) {

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {

    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionDTO>> searchTransactions(

    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<TransactionDTO>> getOngoingTransactions() {

    }

    @GetMapping("/pending")
    public ResponseEntity<List<TransactionDTO>> getPendingTransactions() {

    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<TransactionDTO>> getInProgressTransactions() {

    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionDTO>> filterTransactions(

    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<TransactionDTO> confirmTransaction(@PathVariable String id) {

    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getTransactionDetails(@PathVariable String id) {

    }

    @PostMapping("/batch/complete")
    public ResponseEntity<Integer> completeMultipleTransactions(@RequestBody List<String> transactionIds) {

    }

    @PostMapping("/batch/cancel")
    public ResponseEntity<Integer> cancelMultipleTransactions(@RequestBody List<String> transactionIds) {

    }
}