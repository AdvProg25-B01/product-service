package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TransactionRepository {
    private final Map<String, Transaction> transactionStore = new ConcurrentHashMap<>();

    public Transaction save(Transaction transaction) {
    }

    public Optional<Transaction> findById(String id) {
    }

    public List<Transaction> findAll() {
    }

    public List<Transaction> findByCustomerId(String customerId) {
    }

    public List<Transaction> findByStatus(TransactionStatus status) {
    }

    public List<Transaction> findByPaymentMethod(String paymentMethod) {
    }

    public List<Transaction> findByDateRange(Date startDate, Date endDate) {
    }

    public void delete(String id) {
    }

    public boolean existsById(String id) {
    }
}