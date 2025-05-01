package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.model.Payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
    private final List<Payment> payments = new ArrayList<>();

    public Payment save(Payment payment) {
        return null;
    }

    public Payment findById(String id) {
        return null;
    }

    public Payment findByCustomerId(String customerId) {
        return null;
    }

    public Payment update(Payment payment) {
        return null;
    }

    public boolean delete(Payment payment) {
        return false;
    }
}
