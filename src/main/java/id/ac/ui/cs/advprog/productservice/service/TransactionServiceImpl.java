package id.ac.ui.cs.advprog.productservice.service;

import id.ac.ui.cs.advprog.productservice.dto.TransactionDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionRequestDTO;
import id.ac.ui.cs.advprog.productservice.dto.TransactionUpdateDTO;
import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.model.TransactionItem;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.service.ProductService;
import id.ac.ui.cs.advprog.productservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final ProductService productService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(ProductService productService, TransactionRepository transactionRepository) {
        this.productService = productService;
        this.transactionRepository = transactionRepository;
    }

    private String getProductId(Product product) {
        try {
            var field = product.getClass().getDeclaredField("id");
            field.setAccessible(true);
            return (String) field.get(product);
        } catch (Exception e) {
            return "unknown-" + System.identityHashCode(product);
        }
    }

    @Override
    public TransactionDTO createTransaction(TransactionRequestDTO requestDTO) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setCustomerId(requestDTO.getCustomerId());
        transaction.setPaymentMethod(requestDTO.getPaymentMethod());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(new Date());
        transaction.setUpdatedAt(new Date());

        for (Map.Entry<String, Integer> entry : requestDTO.getProductQuantities().entrySet()) {
            String productId = entry.getKey();
            Integer quantity = entry.getValue();
            if (quantity <= 0) continue;

            Product product = productService.getProductById(productId);
            if (product == null) throw new IllegalArgumentException("Product not found: " + productId);

            if (product.getStock() < quantity)
                throw new IllegalStateException("Not enough stock for product: " + product.getName());

            transaction.addItem(new TransactionItem(product, quantity));

            product.setStock(product.getStock() - quantity);
            productService.editProduct(product, true);
        }

        transaction.calculateTotalAmount();
        transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    public TransactionDTO getTransactionById(String id) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) throw new NoSuchElementException("Transaction not found: " + id);
        return TransactionDTO.fromTransaction(transactionOpt.get());
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByCustomerId(String customerId) {
        return transactionRepository.findByCustomerId(customerId).stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status).stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByPaymentMethod(String paymentMethod) {
        return transactionRepository.findByPaymentMethod(paymentMethod).stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByDateRange(Date start, Date end) {
        return transactionRepository.findByDateRange(start, end).stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO updateTransaction(String id, TransactionUpdateDTO updateDTO) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) throw new NoSuchElementException("Transaction not found: " + id);
        Transaction transaction = transactionOpt.get();

        if (transaction.getStatus() != TransactionStatus.PENDING &&
                transaction.getStatus() != TransactionStatus.IN_PROGRESS)
            throw new IllegalStateException("Cannot update transaction with status: " + transaction.getStatus());

        if (updateDTO.getCustomerId() != null)
            transaction.setCustomerId(updateDTO.getCustomerId());

        if (updateDTO.getPaymentMethod() != null)
            transaction.setPaymentMethod(updateDTO.getPaymentMethod());

        if (updateDTO.getProductQuantities() != null) {
            for (TransactionItem item : transaction.getItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productService.editProduct(product, true);
            }

            transaction.getItems().clear();

            for (Map.Entry<String, Integer> entry : updateDTO.getProductQuantities().entrySet()) {
                String productId = entry.getKey();
                Integer quantity = entry.getValue();

                if (quantity > 0) {
                    Product product = productService.getProductById(productId);
                    if (product == null) throw new IllegalArgumentException("Product not found: " + productId);
                    if (product.getStock() < quantity)
                        throw new IllegalStateException("Not enough stock for product: " + product.getName());

                    transaction.addItem(new TransactionItem(product, quantity));
                    product.setStock(product.getStock() - quantity);
                    productService.editProduct(product, true);
                }
            }
        }

        transaction.calculateTotalAmount();
        transaction.setUpdatedAt(new Date());
        transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    public TransactionDTO completeTransaction(String id) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) throw new NoSuchElementException("Transaction not found: " + id);
        Transaction transaction = transactionOpt.get();

        if (transaction.getStatus() != TransactionStatus.PENDING)
            throw new IllegalStateException("Cannot complete transaction with status: " + transaction.getStatus());

        transaction.complete();
        transaction.setUpdatedAt(new Date());
        transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    public TransactionDTO cancelTransaction(String id) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) throw new NoSuchElementException("Transaction not found: " + id);
        Transaction transaction = transactionOpt.get();

        if (transaction.getStatus() == TransactionStatus.CANCELLED)
            throw new IllegalStateException("Transaction is already cancelled");

        for (TransactionItem item : transaction.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productService.editProduct(product, true);
        }

        transaction.cancel();
        transaction.setUpdatedAt(new Date());
        transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    public void deleteTransaction(String id) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) throw new NoSuchElementException("Transaction not found: " + id);
        Transaction transaction = transactionOpt.get();

        if (transaction.getStatus() != TransactionStatus.CANCELLED) {
            for (TransactionItem item : transaction.getItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productService.editProduct(product, true);
            }
        }

        transactionRepository.delete(id);
    }

    @Override
    public List<TransactionDTO> searchTransactions(String keyword) {
        if (keyword == null || keyword.isEmpty()) return getAllTransactions();

        return transactionRepository.findAll().stream()
                .filter(t -> t.getId().contains(keyword) ||
                        (t.getCustomerId() != null && t.getCustomerId().contains(keyword)))
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getOngoingTransactions() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == TransactionStatus.PENDING ||
                        t.getStatus() == TransactionStatus.IN_PROGRESS)
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> filterTransactions(
            String customerId,
            List<TransactionStatus> statuses,
            List<String> paymentMethods,
            Date startDate,
            Date endDate,
            String sortBy,
            String sortDirection) {

        Stream<Transaction> stream = transactionRepository.findAll().stream();

        if (customerId != null && !customerId.isEmpty()) {
            stream = stream.filter(t -> customerId.equals(t.getCustomerId()));
        }

        if (statuses != null && !statuses.isEmpty()) {
            stream = stream.filter(t -> statuses.contains(t.getStatus()));
        }

        if (paymentMethods != null && !paymentMethods.isEmpty()) {
            stream = stream.filter(t -> paymentMethods.contains(t.getPaymentMethod()));
        }

        if (startDate != null) {
            stream = stream.filter(t -> !t.getCreatedAt().before(startDate));
        }

        if (endDate != null) {
            stream = stream.filter(t -> !t.getCreatedAt().after(endDate));
        }

        List<TransactionDTO> result = stream
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());

        Comparator<TransactionDTO> comparator;
        switch (sortBy.toLowerCase()) {
            case "totalamount":
                comparator = Comparator.comparing(TransactionDTO::getTotalAmount);
                break;
            case "updatedat":
                comparator = Comparator.comparing(TransactionDTO::getUpdatedAt);
                break;
            case "status":
                comparator = Comparator.comparing(dto -> dto.getStatus().name());
                break;
            case "createdat":
            default:
                comparator = Comparator.comparing(TransactionDTO::getCreatedAt);
        }

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        result.sort(comparator);

        return result;
    }

    @Override
    public TransactionDTO confirmTransaction(String id) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) throw new NoSuchElementException("Transaction not found: " + id);
        Transaction transaction = transactionOpt.get();

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Only pending transactions can be confirmed.");
        }

        for (TransactionItem item : transaction.getItems()) {
            Product product = productService.getProductById(item.getProduct().getId());
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }
        }

        transaction.markInProgress();
        transaction.setUpdatedAt(new Date());
        transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    public Map<String, Object> getTransactionDetails(String id) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty()) throw new NoSuchElementException("Transaction not found: " + id);
        Transaction transaction = transactionOpt.get();

        Map<String, Object> details = new HashMap<>();

        details.put("transaction", TransactionDTO.fromTransaction(transaction));

        List<Map<String, Object>> stockStatus = new ArrayList<>();
        for (TransactionItem item : transaction.getItems()) {
            Product product = productService.getProductById(item.getProduct().getId());
            Map<String, Object> productStatus = new HashMap<>();
            productStatus.put("productId", product.getId());
            productStatus.put("productName", product.getName());
            productStatus.put("quantityInTransaction", item.getQuantity());
            productStatus.put("currentStock", product.getStock());
            stockStatus.add(productStatus);
        }
        details.put("stockStatus", stockStatus);

        return details;
    }

    @Override
    public int batchCompleteTransactions(List<String> transactionIds) {
        int completedCount = 0;

        for (String id : transactionIds) {
            try {
                Optional<Transaction> transactionOpt = transactionRepository.findById(id);
                if (transactionOpt.isPresent()) {
                    Transaction transaction = transactionOpt.get();
                    if (transaction.getStatus() == TransactionStatus.PENDING) {
                        transaction.complete();
                        transaction.setUpdatedAt(new Date());
                        transactionRepository.save(transaction);
                        completedCount++;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error completing transaction " + id + ": " + e.getMessage());
            }
        }

        return completedCount;
    }

    @Override
    public int batchCancelTransactions(List<String> transactionIds) {
        int canceledCount = 0;

        for (String id : transactionIds) {
            try {
                Optional<Transaction> transactionOpt = transactionRepository.findById(id);
                if (transactionOpt.isPresent()) {
                    Transaction transaction = transactionOpt.get();
                    if (transaction.getStatus() != TransactionStatus.CANCELLED) {
                        for (TransactionItem item : transaction.getItems()) {
                            Product product = item.getProduct();
                            product.setStock(product.getStock() + item.getQuantity());
                            productService.editProduct(product, true);
                        }

                        transaction.cancel();
                        transaction.setUpdatedAt(new Date());
                        transactionRepository.save(transaction);
                        canceledCount++;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error canceling transaction " + id + ": " + e.getMessage());
            }
        }

        return canceledCount;
    }
}