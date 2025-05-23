package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByCustomerId(String customerId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByPaymentMethod(String paymentMethod);

    @Query("SELECT t FROM Transaction t WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate")
    List<Transaction> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<Transaction> findByStatusIn(List<TransactionStatus> statuses);

    List<Transaction> findByPaymentMethodIn(List<String> paymentMethods);

    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' OR t.status = 'IN_PROGRESS'")
    List<Transaction> findOngoingTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.id LIKE %:keyword% OR t.customerId LIKE %:keyword%")
    List<Transaction> searchByKeyword(@Param("keyword") String keyword);

    List<Transaction> findByCustomerIdAndStatus(String customerId, TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.customerId = :customerId AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    List<Transaction> findByCustomerIdAndDateRange(
            @Param("customerId") String customerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("SELECT t FROM Transaction t " +
            "WHERE (:customerId IS NULL OR t.customerId = :customerId) " +
            "AND (:statuses IS NULL OR t.status IN :statuses) " +
            "AND (:paymentMethods IS NULL OR t.paymentMethod IN :paymentMethods) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    List<Transaction> findTransactionsWithFilters(
            @Param("customerId") String customerId,
            @Param("statuses") List<TransactionStatus> statuses,
            @Param("paymentMethods") List<String> paymentMethods,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    long countByStatus(TransactionStatus status);

    List<Transaction> findByCreatedAtAfter(Date date);

    List<Transaction> findByCreatedAtBefore(Date date);

    @Query("SELECT t FROM Transaction t WHERE t.totalAmount > :amount")
    List<Transaction> findByTotalAmountGreaterThan(@Param("amount") double amount);

    @Query("SELECT t FROM Transaction t WHERE t.totalAmount < :amount")
    List<Transaction> findByTotalAmountLessThan(@Param("amount") double amount);

    @Query("SELECT t FROM Transaction t WHERE t.totalAmount BETWEEN :minAmount AND :maxAmount")
    List<Transaction> findByTotalAmountBetween(@Param("minAmount") double minAmount, @Param("maxAmount") double maxAmount);
}