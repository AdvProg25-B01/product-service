package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByCustomerId(String customerId);
}
