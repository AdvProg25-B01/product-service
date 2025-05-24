package id.ac.ui.cs.advprog.productservice.dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRequestDTOTest {

    @Test
    void testEmptyConstructor() {
        TransactionRequestDTO dto = new TransactionRequestDTO();

        assertNull(dto.getCustomerId());
        assertNull(dto.getProductQuantities());
        assertNull(dto.getAmount());
        assertNull(dto.getPaymentMethod());
    }

    @Test
    void testSettersAndGetters() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        String customerId = "customer-123";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product-1", 2);
        productQuantities.put("product-2", 3);
        double amount = 150.75;
        String paymentMethod = "CASH";

        dto.setCustomerId(customerId);
        dto.setProductQuantities(productQuantities);
        dto.setAmount(amount);
        dto.setPaymentMethod(paymentMethod);

        assertEquals(customerId, dto.getCustomerId());
        assertEquals(productQuantities, dto.getProductQuantities());
        assertEquals(amount, dto.getAmount());
        assertEquals(paymentMethod, dto.getPaymentMethod());
    }

    @Test
    void testProductQuantitiesMap() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        Map<String, Integer> productQuantities = new HashMap<>();

        productQuantities.put("product-1", 2);
        dto.setProductQuantities(productQuantities);

        assertEquals(1, dto.getProductQuantities().size());
        assertEquals(2, dto.getProductQuantities().get("product-1"));

        dto.getProductQuantities().put("product-2", 3);
        dto.getProductQuantities().put("product-1", 5);

        assertEquals(2, dto.getProductQuantities().size());
        assertEquals(5, dto.getProductQuantities().get("product-1"));
        assertEquals(3, dto.getProductQuantities().get("product-2"));
    }

    @Test
    void testEqualsAndHashCode() {
        TransactionRequestDTO dto1 = new TransactionRequestDTO();
        dto1.setCustomerId("customer-123");
        dto1.setAmount(100.0);
        dto1.setPaymentMethod("CASH");
        Map<String, Integer> productQuantities1 = new HashMap<>();
        productQuantities1.put("product-1", 2);
        dto1.setProductQuantities(productQuantities1);

        TransactionRequestDTO dto2 = new TransactionRequestDTO();
        dto2.setCustomerId("customer-123");
        dto2.setAmount(100.0);
        dto2.setPaymentMethod("CASH");
        Map<String, Integer> productQuantities2 = new HashMap<>();
        productQuantities2.put("product-1", 2);
        dto2.setProductQuantities(productQuantities2);

        TransactionRequestDTO dto3 = new TransactionRequestDTO();
        dto3.setCustomerId("different-customer");
        dto3.setAmount(100.0);
        dto3.setPaymentMethod("CASH");
        dto3.setProductQuantities(productQuantities1);

        TransactionRequestDTO dto4 = new TransactionRequestDTO();
        dto4.setCustomerId("customer-123");
        dto4.setAmount(200.0);
        dto4.setPaymentMethod("CASH");
        dto4.setProductQuantities(productQuantities1);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, dto4);
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);

        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setCustomerId("customer-123");
        dto.setAmount(150.75);
        dto.setPaymentMethod("CASH");
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product-1", 2);
        dto.setProductQuantities(productQuantities);

        String result = dto.toString();

        assertNotNull(result);
        assertTrue(result.contains("customer-123"));
        assertTrue(result.contains("150.75"));
        assertTrue(result.contains("CASH"));
        assertTrue(result.contains("product-1"));
    }

    @Test
    void testMapManipulation() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product-1", 2);
        dto.setProductQuantities(productQuantities);

        Map<String, Integer> retrievedMap = dto.getProductQuantities();
        retrievedMap.put("product-2", 3);

        assertEquals(2, dto.getProductQuantities().size());
        assertEquals(3, dto.getProductQuantities().get("product-2"));

        Map<String, Integer> newMap = new HashMap<>();
        newMap.put("product-3", 4);
        dto.setProductQuantities(newMap);

        retrievedMap.put("product-4", 5);
        assertEquals(1, dto.getProductQuantities().size());
        assertNull(dto.getProductQuantities().get("product-4"));
    }

    @Test
    void testAmountField() {
        TransactionRequestDTO dto = new TransactionRequestDTO();

        assertNull(dto.getAmount());

        dto.setAmount(99.99);
        assertEquals(99.99, dto.getAmount());

        dto.setAmount(0.0);
        assertEquals(0.0, dto.getAmount());
    }

    @Test
    void testNullProductQuantities() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setProductQuantities(null);
        assertNull(dto.getProductQuantities());
    }

    @Test
    void testEmptyProductQuantities() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        Map<String, Integer> emptyMap = new HashMap<>();
        dto.setProductQuantities(emptyMap);

        assertNotNull(dto.getProductQuantities());
        assertEquals(0, dto.getProductQuantities().size());
        assertTrue(dto.getProductQuantities().isEmpty());
    }
}