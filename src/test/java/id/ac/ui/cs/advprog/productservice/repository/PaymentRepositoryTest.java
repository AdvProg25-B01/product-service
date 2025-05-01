package id.ac.ui.cs.advprog.productservice.repository;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class PaymentRepositoryTest {

    private PaymentRepository repository;
    private Payment payment;

    @BeforeEach
    void setUp() {
        repository = new PaymentRepository();
        payment = new Payment("p1", "c1", 100.0, "OVO", "PENDING", new Date());
        repository.save(payment);
        repository.clear();
    }

    @Test
    void testSavePaymentSuccessfully() {
        Payment newPayment = new Payment("p2", "c2", 150.0, "DANA", "PAID", new Date());
        Payment saved = repository.save(newPayment);
        assertEquals("p2", saved.getId());
    }

    @Test
    void testFindByIdSuccessfully() {
        Payment payment = new Payment("p1", "c1", 100.0, "OVO", "PENDING", new Date());
        repository.save(payment);

        Payment foundPayment = repository.findById("p1");

        assertNotNull(foundPayment);
        assertEquals("p1", foundPayment.getId());
    }

    @Test
    void testFindByCustomerIdSuccessfully() {
        Payment payment1 = new Payment("p1", "c1", 100.0, "OVO", "PENDING", new Date());
        repository.save(payment1);

        List<Payment> foundPayments = repository.findByCustomerId("c1");

        assertNotNull(foundPayments);
        assertEquals(1, foundPayments.size());
        assertEquals("p1", foundPayments.get(0).getId());
    }

    @Test
    void testUpdateSuccessfully() {
        Payment payment = new Payment("p1", "c1", 100.0, "OVO", "PENDING", new Date());
        repository.save(payment);

        payment.setStatus("COMPLETED");
        Payment updatedPayment = repository.update(payment);

        assertNotNull(updatedPayment);
        assertEquals("COMPLETED", updatedPayment.getStatus());
    }

    @Test
    void testDeleteSuccessfully() {
        Payment payment = new Payment("p1", "c1", 100.0, "OVO", "PENDING", new Date());
        repository.save(payment);

        Payment foundPayment = repository.findById("p1");
        assertNotNull(foundPayment);

        boolean deleted = repository.delete(payment);
        assertTrue(deleted);

        Payment deletedPayment = repository.findById("p1");
        assertNull(deletedPayment);
    }


    @Test
    void testFindByIdNotFound() {
        Payment notFound = repository.findById("nonexistent");
        assertNull(notFound);
    }

    @Test
    void testFindByCustomerIdNotFound() {
        List<Payment> notFound = repository.findByCustomerId("unknown");
        assertNotNull(notFound);
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testUpdateNonExistentPayment() {
        Payment fakePayment = new Payment("px", "cx", 0.0, "GOPAY", "PENDING", new Date());
        Payment result = repository.update(fakePayment);
        assertNull(result);
    }

    @Test
    void testDeleteNonExistentPayment() {
        Payment nonExistentPayment = new Payment("p99", "c99", 100.0, "OVO", "PENDING", new Date());

        assertThrows(RuntimeException.class, () -> {
            repository.delete(nonExistentPayment);
        });
    }
}
