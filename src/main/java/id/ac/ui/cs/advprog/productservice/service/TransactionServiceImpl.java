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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
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
            return product.getId().toString();
        } catch (Exception e) {
            return "unknown-" + System.identityHashCode(product);
        }
    }

    @Override
    @Transactional
    public TransactionDTO createTransaction(TransactionRequestDTO requestDTO) {
        Transaction transaction = new Transaction();
        transaction.setCustomerId(requestDTO.getCustomerId());
        transaction.setPaymentMethod(requestDTO.getPaymentMethod());
        transaction.setStatus(TransactionStatus.PENDING);

        for (Map.Entry<String, Integer> entry : requestDTO.getProductQuantities().entrySet()) {
            String productId = entry.getKey();
            Integer quantity = entry.getValue();
            if (quantity <= 0) continue;

            Product product = productService
                    .getProductById(productId)
                    .orElseThrow(() -> new NoSuchElementException(
                            "Product not found: " + productId));

            if (product.getStock() < quantity)
                throw new IllegalStateException("Not enough stock for product: " + product.getName());

            TransactionItem transactionItem = new TransactionItem(product, quantity);
            transaction.addItem(transactionItem);

            product.setStock(product.getStock() - quantity);
            productService.editProduct(product, true);
        }

        transaction.calculateTotalAmount();
        transaction = transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + id));
        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByCustomerId(String customerId) {
        return transactionRepository.findByCustomerId(customerId).stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status).stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByPaymentMethod(String paymentMethod) {
        return transactionRepository.findByPaymentMethod(paymentMethod).stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByDateRange(Date start, Date end) {
        return transactionRepository.findByDateRange(start, end).stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(String id, TransactionUpdateDTO updateDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + id));

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
                    Product product = productService
                            .getProductById(productId)
                            .orElseThrow(() -> new NoSuchElementException(
                                    "Product not found: " + productId));

                    if (product.getStock() < quantity)
                        throw new IllegalStateException("Not enough stock for product: " + product.getName());

                    TransactionItem transactionItem = new TransactionItem(product, quantity);
                    transaction.addItem(transactionItem);
                    product.setStock(product.getStock() - quantity);
                    productService.editProduct(product, true);
                }
            }
        }

        transaction.calculateTotalAmount();
        transaction.setUpdatedAt(new Date());
        transaction = transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO completeTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + id));

        if (transaction.getStatus() != TransactionStatus.PENDING)
            throw new IllegalStateException("Cannot complete transaction with status: " + transaction.getStatus());

        transaction.complete();
        transaction = transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO cancelTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + id));

        if (transaction.getStatus() == TransactionStatus.CANCELLED)
            throw new IllegalStateException("Transaction is already cancelled");

        for (TransactionItem item : transaction.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productService.editProduct(product, true);
        }

        transaction.cancel();
        transaction = transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + id));

        if (transaction.getStatus() != TransactionStatus.CANCELLED) {
            for (TransactionItem item : transaction.getItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productService.editProduct(product, true);
            }
        }

        transactionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> searchTransactions(String keyword) {
        if (keyword == null || keyword.isEmpty())
            return getAllTransactions();

        return transactionRepository.searchByKeyword(keyword).stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getOngoingTransactions() {
        return transactionRepository.findOngoingTransactions().stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> filterTransactions(
            String customerId,
            List<TransactionStatus> statuses,
            List<String> paymentMethods,
            Date startDate,
            Date endDate,
            String sortBy,
            String sortDirection) {

        List<Transaction> transactions = transactionRepository.findTransactionsWithFilters(
                customerId, statuses, paymentMethods, startDate, endDate);

        List<TransactionDTO> result = transactions.stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());

        Comparator<TransactionDTO> comparator;
        switch (sortBy != null ? sortBy.toLowerCase() : "createdat") {
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
    @Transactional
    public TransactionDTO confirmTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + id));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Only pending transactions can be confirmed.");
        }

        for (TransactionItem item : transaction.getItems()) {
            Product product = productService
                    .getProductById(item.getProduct().getId().toString())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Product not found: " + item.getProduct().getId()));
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }
        }

        transaction.markInProgress();
        transaction = transactionRepository.save(transaction);

        return TransactionDTO.fromTransaction(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTransactionDetails(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + id));

        Map<String, Object> details = new HashMap<>();
        details.put("transaction", TransactionDTO.fromTransaction(transaction));

        List<Map<String, Object>> stockStatus = new ArrayList<>();
        for (TransactionItem item : transaction.getItems()) {
            Product product = productService
                    .getProductById(item.getProduct().getId().toString())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Product not found: " + item.getProduct().getId()));

            Map<String, Object> productStatus = new HashMap<>();
            productStatus.put("productId", product.getId().toString());
            productStatus.put("productName", product.getName());
            productStatus.put("quantityInTransaction", item.getQuantity());
            productStatus.put("currentStock", product.getStock());
            stockStatus.add(productStatus);
        }
        details.put("stockStatus", stockStatus);

        return details;
    }

    @Override
    @Transactional
    public int batchCompleteTransactions(List<String> transactionIds) {
        int completedCount = 0;

        for (String id : transactionIds) {
            try {
                Optional<Transaction> transactionOpt = transactionRepository.findById(id);
                if (transactionOpt.isPresent()) {
                    Transaction transaction = transactionOpt.get();
                    if (transaction.getStatus() == TransactionStatus.PENDING) {
                        transaction.complete();
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
    @Transactional
    public int batchCancelTransactions(List<String> transactionIds) {
        int canceledCount = 0;

        for (String id : transactionIds) {
            try {
                Optional<Transaction> transactionOpt = transactionRepository.findById(id);
                if (transactionOpt.isPresent()) {
                    Transaction transaction = transactionOpt.get();
                    if (transaction.getStatus() != TransactionStatus.CANCELLED) {
                        // Restore stock
                        for (TransactionItem item : transaction.getItems()) {
                            Product product = item.getProduct();
                            product.setStock(product.getStock() + item.getQuantity());
                            productService.editProduct(product, true);
                        }

                        transaction.cancel();
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