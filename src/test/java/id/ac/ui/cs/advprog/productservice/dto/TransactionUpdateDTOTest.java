package id.ac.ui.cs.advprog.productservice.dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionUpdateDTOTest {

    @Test
    void testEmptyConstructor() {
        // Act
        TransactionUpdateDTO dto = new TransactionUpdateDTO();

        // Assert
        assertNull(dto.getCustomerId());
        assertNull(dto.getProductQuantities());
        assertNull(dto.getPaymentMethod());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        String customerId = "updated-customer-123";
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product-1", 5);
        productQuantities.put("product-2", 3);
        String paymentMethod = "CARD";

        // Act
        dto.setCustomerId(customerId);
        dto.setProductQuantities(productQuantities);
        dto.setPaymentMethod(paymentMethod);

        // Assert
        assertEquals(customerId, dto.getCustomerId());
        assertEquals(productQuantities, dto.getProductQuantities());
        assertEquals(paymentMethod, dto.getPaymentMethod());
    }

    @Test
    void testPartialUpdate() {
        // Arrange - Create DTO with only some fields set
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setCustomerId("updated-customer-456");

        // Assert - Only customerId should be set
        assertEquals("updated-customer-456", dto.getCustomerId());
        assertNull(dto.getProductQuantities());
        assertNull(dto.getPaymentMethod());

        // Act - Update another field
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product-3", 2);
        dto.setProductQuantities(productQuantities);

        // Assert - Now both fields should be set
        assertEquals("updated-customer-456", dto.getCustomerId());
        assertEquals(productQuantities, dto.getProductQuantities());
        assertNull(dto.getPaymentMethod());
    }

    @Test
    void testProductQuantitiesMap() {
        // Arrange
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        Map<String, Integer> productQuantities = new HashMap<>();

        // Act - Add items to the map
        productQuantities.put("product-1", 4);
        dto.setProductQuantities(productQuantities);

        // Assert - Initial state
        assertEquals(1, dto.getProductQuantities().size());
        assertEquals(4, dto.getProductQuantities().get("product-1"));

        // Act - Modify the map
        dto.getProductQuantities().put("product-2", 7);
        dto.getProductQuantities().remove("product-1");

        // Assert - After modification
        assertEquals(1, dto.getProductQuantities().size());
        assertNull(dto.getProductQuantities().get("product-1"));
        assertEquals(7, dto.getProductQuantities().get("product-2"));
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        TransactionUpdateDTO dto1 = new TransactionUpdateDTO();
        dto1.setCustomerId("customer-123");
        dto1.setPaymentMethod("CARD");
        Map<String, Integer> productQuantities1 = new HashMap<>();
        productQuantities1.put("product-1", 3);
        dto1.setProductQuantities(productQuantities1);

        TransactionUpdateDTO dto2 = new TransactionUpdateDTO();
        dto2.setCustomerId("customer-123");
        dto2.setPaymentMethod("CARD");
        Map<String, Integer> productQuantities2 = new HashMap<>();
        productQuantities2.put("product-1", 3);
        dto2.setProductQuantities(productQuantities2);

        TransactionUpdateDTO dto3 = new TransactionUpdateDTO();
        dto3.setCustomerId("different-customer");
        dto3.setPaymentMethod("CARD");
        dto3.setProductQuantities(productQuantities1);

        // Act & Assert - equals
        assertEquals(dto1, dto2); // Same values
        assertNotEquals(dto1, dto3); // Different customer
        assertNotEquals(dto1, null); // Null comparison
        assertNotEquals(dto1, new Object()); // Different types
        assertEquals(dto1, dto1); // Same instance

        // Act & Assert - hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        dto.setCustomerId("customer-123");
        dto.setPaymentMethod("CARD");
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product-1", 3);
        dto.setProductQuantities(productQuantities);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("customer-123"));
        assertTrue(result.contains("CARD"));
        assertTrue(result.contains("product-1"));
        assertTrue(result.contains("3"));
    }

    @Test
    void testNullProductQuantities() {
        // Arrange
        TransactionUpdateDTO dto = new TransactionUpdateDTO();

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> dto.setProductQuantities(null));
        assertNull(dto.getProductQuantities());
    }

    @Test
    void testEmptyProductQuantities() {
        // Arrange
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        Map<String, Integer> emptyMap = new HashMap<>();

        // Act
        dto.setProductQuantities(emptyMap);

        // Assert
        assertNotNull(dto.getProductQuantities());
        assertTrue(dto.getProductQuantities().isEmpty());
    }

    @Test
    void testMapManipulation() {
        // Arrange
        TransactionUpdateDTO dto = new TransactionUpdateDTO();
        Map<String, Integer> productQuantities = new HashMap<>();
        productQuantities.put("product-1", 2);
        dto.setProductQuantities(productQuantities);

        // Act - Get a reference to the map and modify it
        Map<String, Integer> retrievedMap = dto.getProductQuantities();
        retrievedMap.put("product-2", 5);

        // Assert - Changes should be reflected in the DTO
        assertEquals(2, dto.getProductQuantities().size());
        assertEquals(5, dto.getProductQuantities().get("product-2"));

        // Act - Replace the map
        Map<String, Integer> newMap = new HashMap<>();
        newMap.put("product-3", 8);
        dto.setProductQuantities(newMap);

        // Assert - Original reference should no longer affect the DTO
        retrievedMap.put("product-4", 10);
        assertEquals(1, dto.getProductQuantities().size());
        assertEquals(8, dto.getProductQuantities().get("product-3"));
        assertNull(dto.getProductQuantities().get("product-4"));
    }
}