package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentRepositoryTest {

    private PaymentRepository repository;
    private Payment payment;

    @BeforeEach
    void setUp() {
        repository = new PaymentRepository();
        payment = new Payment("p1", "c1", 100.0, "OVO", "PENDING", new Date());
        repository.save(payment);
    }

    @Test
    void testSavePaymentSuccessfully() {
        Payment newPayment = new Payment("p2", "c2", 150.0, "DANA", "PAID", new Date());
        Payment saved = repository.save(newPayment);
        assertEquals("p2", saved.getId());
    }

    @Test
    void testFindByIdSuccessfully() {
        Payment found = repository.findById("p1");
        assertNotNull(found);
        assertEquals("c1", found.getCustomerId());
    }

    @Test
    void testFindByCustomerIdSuccessfully() {
        Payment found = repository.findByCustomerId("c1");
        assertNotNull(found);
        assertEquals("p1", found.getId());
    }

    @Test
    void testUpdateSuccessfully() {
        payment.setStatus("PAID");
        Payment updated = repository.update(payment);
        assertEquals("PAID", updated.getStatus());
    }

    @Test
    void testDeleteSuccessfully() {
        boolean deleted = repository.delete(payment);
        assertTrue(deleted);
    }

    @Test
    void testFindByIdNotFound() {
        Payment notFound = repository.findById("nonexistent");
        assertNull(notFound);
    }

    @Test
    void testFindByCustomerIdNotFound() {
        Payment notFound = repository.findByCustomerId("unknown");
        assertNull(notFound);
    }

    @Test
    void testUpdateNonExistentPayment() {
        Payment fakePayment = new Payment("px", "cx", 0.0, "GOPAY", "PENDING", new Date());
        Payment result = repository.update(fakePayment);
        assertNull(result);
    }

    @Test
    void testDeleteNonExistentPayment() {
        Payment fakePayment = new Payment("px", "cx", 0.0, "GOPAY", "PENDING", new Date());
        boolean deleted = repository.delete(fakePayment);
        assertFalse(deleted);
    }
}
