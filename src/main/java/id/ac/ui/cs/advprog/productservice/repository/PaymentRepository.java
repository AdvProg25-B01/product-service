package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.model.Payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {

    private final List<Payment> payments = new ArrayList<>();

    public Payment save(Payment payment) {
        payments.add(payment);
        return payment;
    }

    public Payment findById(String id) {
        return payments.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Payment findByCustomerId(String customerId) {
        return payments.stream()
                .filter(p -> p.getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);
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
        return payments.removeIf(p -> p.getId().equals(payment.getId()));
    }
}
