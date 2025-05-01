package id.ac.ui.cs.advprog.productservice.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    @Test
    void testAllArgsConstructor() {
        Date now = new Date();
        Payment payment = new Payment("p1", "c1", 100.0, "OVO", "PENDING", now);

        assertEquals("p1", payment.getId());
        assertEquals("c1", payment.getCustomerId());
        assertEquals(100.0, payment.getAmount());
        assertEquals("OVO", payment.getMethod());
        assertEquals("PENDING", payment.getStatus());
        assertEquals(now, payment.getCreatedAt());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Payment payment = new Payment();
        Date now = new Date();

        payment.setId("p2");
        payment.setCustomerId("c2");
        payment.setAmount(200.0);
        payment.setMethod("GoPay");
        payment.setStatus("PAID");

        assertEquals("p2", payment.getId());
        assertEquals("c2", payment.getCustomerId());
        assertEquals(200.0, payment.getAmount());
        assertEquals("GoPay", payment.getMethod());
        assertEquals("PAID", payment.getStatus());
        assertNotNull(payment.getCreatedAt());
    }

    @Test
    void testNegativeAmountShouldBeAllowedButSuspicious() {
        Payment payment = new Payment();
        payment.setAmount(-50.0);

        assertEquals(-50.0, payment.getAmount());
    }

}
