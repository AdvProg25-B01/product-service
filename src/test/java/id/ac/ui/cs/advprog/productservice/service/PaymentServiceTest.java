package id.ac.ui.cs.advprog.productservice.service;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment("p1", "c1", 100.0, "OVO", "PENDING", new Date());
    }


    @Test
    void testCreatePayment_success() {
        when(paymentRepository.save(payment)).thenReturn(payment);

        assertDoesNotThrow(() -> paymentService.createPayment(payment));
        verify(paymentRepository).save(payment);
    }

    @Test
    void testGetPaymentById_success() {
        when(paymentRepository.findById("p1")).thenReturn(payment);

        Payment result = paymentService.getPaymentById("p1");
        assertEquals(payment, result);
    }

    @Test
    void testGetPaymentsByCustomerId_success() {
        when(paymentRepository.findByCustomerId("c1")).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getPaymentsByCustomerId("c1");
        assertEquals(1, result.size());
        assertEquals(payment, result.get(0));
    }

    @Test
    void testUpdatePaymentStatus_success() {
        when(paymentRepository.findById("p1")).thenReturn(payment);

        assertDoesNotThrow(() -> paymentService.updatePaymentStatus("p1", "PAID"));
        assertEquals("PAID", payment.getStatus());
    }

    @Test
    void testDeletePayment_success() {
        when(paymentRepository.findById("p1")).thenReturn(payment);
        when(paymentRepository.delete(payment)).thenReturn(true);

        assertDoesNotThrow(() -> paymentService.deletePayment("p1"));
        verify(paymentRepository).delete(payment);
    }


    @Test
    void testGetPaymentById_notFound() {
        when(paymentRepository.findById("invalid")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.getPaymentById("invalid");
        });
        assertEquals("Payment not found", exception.getMessage());
    }

    @Test
    void testUpdatePaymentStatus_notFound() {
        when(paymentRepository.findById("invalid")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.updatePaymentStatus("invalid", "PAID");
        });
        assertEquals("Payment not found", exception.getMessage());
    }

    @Test
    void testDeletePayment_notFound() {
        when(paymentRepository.findById("invalid")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.deletePayment("invalid");
        });
        assertEquals("Payment not found", exception.getMessage());
    }

    @Test
    void testDeletePayment_failsToDelete() {
        when(paymentRepository.findById("p1")).thenReturn(payment);
        when(paymentRepository.delete(payment)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.deletePayment("p1");
        });
        assertEquals("Failed to delete payment", exception.getMessage());
    }
}
