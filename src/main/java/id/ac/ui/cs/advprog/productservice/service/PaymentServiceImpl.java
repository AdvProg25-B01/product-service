package id.ac.ui.cs.advprog.productservice.service;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.repository.PaymentRepository;

import java.util.List;
import java.util.Objects;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    public PaymentServiceImpl(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createPayment(Payment payment) {
        repository.save(payment);
    }

    @Override
    public Payment getPaymentById(String paymentId) {
        Payment payment = repository.findById(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }
        return payment;
    }

    @Override
    public List<Payment> getPaymentsByCustomerId(String customerId) {
        return (List<Payment>) repository.findByCustomerId(customerId);
    }

    @Override
    public void updatePaymentStatus(String paymentId, String status) {
        Payment payment = repository.findById(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }
        payment.setStatus(status);
        repository.update(payment);
    }

    @Override
    public void deletePayment(String paymentId) {
        Payment payment = repository.findById(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment with ID " + paymentId + " not found");
        }
        repository.delete(payment);
    }

}
