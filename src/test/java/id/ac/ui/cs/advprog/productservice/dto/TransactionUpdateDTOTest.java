package id.ac.ui.cs.advprog.productservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionUpdateDTOTest {

    @Test
    void testEmptyConstructor() {
        TransactionUpdateDTO dto = new TransactionUpdateDTO();

        assertNull(dto.getCustomerId());
        assertNull(dto.getAmount());
        assertNull(dto.getPaymentMethod());
    }

    @Test
    void testSettersAndGetters() {
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        String customerId = "updated-customer-123";
        Double amount = 500.0;
        String paymentMethod = "CARD";

        dto.setCustomerId(customerId);
        dto.setAmount(amount);
        dto.setPaymentMethod(paymentMethod);

        assertEquals(customerId, dto.getCustomerId());
        assertEquals(amount, dto.getAmount());
        assertEquals(paymentMethod, dto.getPaymentMethod());
    }

    @Test
    void testPartialUpdate() {
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setCustomerId("updated-customer-456");

        assertEquals("updated-customer-456", dto.getCustomerId());
        assertNull(dto.getAmount());
        assertNull(dto.getPaymentMethod());

        dto.setAmount(750.0);

        assertEquals("updated-customer-456", dto.getCustomerId());
        assertEquals(750.0, dto.getAmount());
        assertNull(dto.getPaymentMethod());
    }

    @Test
    void testAmountField() {
        TransactionUpdateDTO dto = new TransactionUpdateDTO();

        dto.setAmount(100.0);

        assertEquals(100.0, dto.getAmount());

        dto.setAmount(0.0);

        assertEquals(0.0, dto.getAmount());

        dto.setAmount(-50.0);

        assertEquals(-50.0, dto.getAmount());
    }

    @Test
    void testEqualsAndHashCode() {
        TransactionUpdateDTO dto1 = new TransactionUpdateDTO();
        dto1.setCustomerId("customer-123");
        dto1.setAmount(200.0);
        dto1.setPaymentMethod("CARD");

        TransactionUpdateDTO dto2 = new TransactionUpdateDTO();
        dto2.setCustomerId("customer-123");
        dto2.setAmount(200.0);
        dto2.setPaymentMethod("CARD");

        TransactionUpdateDTO dto3 = new TransactionUpdateDTO();
        dto3.setCustomerId("different-customer");
        dto3.setAmount(200.0);
        dto3.setPaymentMethod("CARD");

        TransactionUpdateDTO dto4 = new TransactionUpdateDTO();
        dto4.setCustomerId("customer-123");
        dto4.setAmount(300.0);
        dto4.setPaymentMethod("CARD");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, dto4);
        assertNotEquals(null, dto1);
        assertNotEquals(dto1, new Object());
        assertEquals(dto1, dto1);

        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        assertNotEquals(dto1.hashCode(), dto4.hashCode());
    }

    @Test
    void testToString() {
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setCustomerId("customer-123");
        dto.setAmount(350.0);
        dto.setPaymentMethod("CARD");

        String result = dto.toString();

        assertNotNull(result);
        assertTrue(result.contains("customer-123"));
        assertTrue(result.contains("350.0"));
        assertTrue(result.contains("CARD"));
    }

    @Test
    void testNullAmount() {
        TransactionUpdateDTO dto = new TransactionUpdateDTO();

        assertDoesNotThrow(() -> dto.setAmount(null));
        assertNull(dto.getAmount());
    }

    @Test
    void testNullFields() {
        TransactionUpdateDTO dto = new TransactionUpdateDTO();

        assertDoesNotThrow(() -> dto.setCustomerId(null));
        assertDoesNotThrow(() -> dto.setAmount(null));
        assertDoesNotThrow(() -> dto.setPaymentMethod(null));

        assertNull(dto.getCustomerId());
        assertNull(dto.getAmount());
        assertNull(dto.getPaymentMethod());
    }

    @Test
    void testPaymentMethodValues() {
        TransactionUpdateDTO dto = new TransactionUpdateDTO();

        dto.setPaymentMethod("CASH");
        assertEquals("CASH", dto.getPaymentMethod());

        dto.setPaymentMethod("CARD");
        assertEquals("CARD", dto.getPaymentMethod());

        dto.setPaymentMethod("INSTALLMENT");
        assertEquals("INSTALLMENT", dto.getPaymentMethod());

        dto.setPaymentMethod("BANK_TRANSFER");
        assertEquals("BANK_TRANSFER", dto.getPaymentMethod());
    }

    @Test
    void testAllFieldsCombination() {
        TransactionUpdateDTO dto = new TransactionUpdateDTO();

        dto.setCustomerId("customer-789");
        dto.setAmount(999.99);
        dto.setPaymentMethod("INSTALLMENT");

        assertEquals("customer-789", dto.getCustomerId());
        assertEquals(999.99, dto.getAmount());
        assertEquals("INSTALLMENT", dto.getPaymentMethod());

        dto.setCustomerId(null);
        dto.setAmount(null);
        dto.setPaymentMethod(null);

        assertNull(dto.getCustomerId());
        assertNull(dto.getAmount());
        assertNull(dto.getPaymentMethod());
    }
}