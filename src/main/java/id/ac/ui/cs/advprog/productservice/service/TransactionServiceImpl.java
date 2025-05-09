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

    }

    @Override
    public TransactionDTO getTransactionById(String id) {

    }

    @Override
    public List<TransactionDTO> getAllTransactions() {

    }

    @Override
    public List<TransactionDTO> getTransactionsByCustomerId(String customerId) {

    }

    @Override
    public List<TransactionDTO> getTransactionsByStatus(TransactionStatus status) {

    }

    @Override
    public List<TransactionDTO> getTransactionsByPaymentMethod(String paymentMethod) {

    }

    @Override
    public List<TransactionDTO> getTransactionsByDateRange(Date start, Date end) {

    }

    @Override
    public TransactionDTO updateTransaction(String id, TransactionUpdateDTO updateDTO) {

    }

    @Override
    public TransactionDTO completeTransaction(String id) {

    }

    @Override
    public TransactionDTO cancelTransaction(String id) {

    }

    @Override
    public void deleteTransaction(String id) {

    }

    @Override
    public List<TransactionDTO> searchTransactions(String keyword) {

    }

    @Override
    public List<TransactionDTO> getOngoingTransactions() {

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


    }

    @Override
    public TransactionDTO confirmTransaction(String id) {

    }

    @Override
    public Map<String, Object> getTransactionDetails(String id) {

    }

    @Override
    public int batchCompleteTransactions(List<String> transactionIds) {

    }

    @Override
    public int batchCancelTransactions(List<String> transactionIds) {

    }
}