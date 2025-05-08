package id.ac.ui.cs.advprog.productservice.service;

import id.ac.ui.cs.advprog.productservice.model.Payment;

import java.util.List;

public interface PaymentService {
    void createPayment(Payment payment);
    Payment getPaymentById(String paymentId);
    List<Payment> getPaymentsByCustomerId(String customerId);
    void updatePaymentStatus(String paymentId, String status);
    void deletePayment(String paymentId);
}
