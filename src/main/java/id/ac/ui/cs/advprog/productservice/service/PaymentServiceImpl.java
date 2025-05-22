package id.ac.ui.cs.advprog.productservice.service;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.repository.PaymentRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    public PaymentServiceImpl(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment createPayment(Payment payment) {
        payment.setId(null);
        return repository.save(payment);
    }

    @Override
    public Payment getPaymentById(String paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
    }

    @Override
    public List<Payment> getPaymentsByCustomerId(String customerId) {
        return (List<Payment>) repository.findByCustomerId(customerId);
    }

    @Override
    public void updatePaymentStatus(String paymentId, String status) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        payment.setStatus(status);
        repository.save(payment);
    }

    @Override
    public void deletePayment(String paymentId) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        repository.delete(payment);
    }
}
