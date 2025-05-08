package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepository {

    private final List<Payment> payments = new ArrayList<>();

    public Payment save(Payment payment) {
        for (Payment existingPayment : payments) {
            if (existingPayment.getId().equals(payment.getId())) {
                throw new RuntimeException("Payment with this ID already exists");
            }
        }
        payments.add(payment);
        return payment;
    }

    public Payment findById(String id) {
        return payments.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Payment> findByCustomerId(String customerId) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments) {
            if (p.getCustomerId().equals(customerId)) {
                result.add(p);
            }
        }
        return result;
    }

    public Payment update(Payment payment) {
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getId().equals(payment.getId())) {
                payments.set(i, payment);
                return payment;
            }
        }
        return null;
    }

    public boolean delete(Payment payment) {
        if (payments.stream().noneMatch(p -> p.getId().equals(payment.getId()))) {
            throw new RuntimeException("Payment not found");
        }
        return payments.removeIf(p -> p.getId().equals(payment.getId()));
    }

    public void clear() {
        payments.clear();
    }

}
