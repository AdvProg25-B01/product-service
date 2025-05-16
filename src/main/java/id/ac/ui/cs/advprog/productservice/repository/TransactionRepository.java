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
        transactionStore.put(transaction.getId(), transaction);
        return transaction;
    }

    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactionStore.get(id));
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactionStore.values());
    }

    public List<Transaction> findByCustomerId(String customerId) {
        return transactionStore.values().stream()
                .filter(transaction -> customerId.equals(transaction.getCustomerId()))
                .collect(Collectors.toList());
    }

    public List<Transaction> findByStatus(TransactionStatus status) {
        return transactionStore.values().stream()
                .filter(transaction -> status.equals(transaction.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Transaction> findByPaymentMethod(String paymentMethod) {
        return transactionStore.values().stream()
                .filter(transaction -> paymentMethod.equals(transaction.getPaymentMethod()))
                .collect(Collectors.toList());
    }

    public List<Transaction> findByDateRange(Date startDate, Date endDate) {
        return transactionStore.values().stream()
                .filter(transaction ->
                        !transaction.getCreatedAt().before(startDate) &&
                                !transaction.getCreatedAt().after(endDate))
                .collect(Collectors.toList());
    }

    public void delete(String id) {
        transactionStore.remove(id);
    }

    public boolean existsById(String id) {
        return transactionStore.containsKey(id);
    }
}