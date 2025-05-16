package id.ac.ui.cs.advprog.productservice.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStatusTest {

    @Test
    void testEnumValues() {
        TransactionStatus[] values = TransactionStatus.values();
        assertEquals(4, values.length);

        assertEquals(TransactionStatus.PENDING, values[0]);
        assertEquals(TransactionStatus.IN_PROGRESS, values[1]);
        assertEquals(TransactionStatus.COMPLETED, values[2]);
        assertEquals(TransactionStatus.CANCELLED, values[3]);
    }

    @Test
    void testValueOf() {
        assertEquals(TransactionStatus.PENDING, TransactionStatus.valueOf("PENDING"));
        assertEquals(TransactionStatus.IN_PROGRESS, TransactionStatus.valueOf("IN_PROGRESS"));
        assertEquals(TransactionStatus.COMPLETED, TransactionStatus.valueOf("COMPLETED"));
        assertEquals(TransactionStatus.CANCELLED, TransactionStatus.valueOf("CANCELLED"));
    }

    @Test
    void testValueOf_InvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionStatus.valueOf("INVALID_STATUS");
        });
    }

    @Test
    void testToString() {
        assertEquals("PENDING", TransactionStatus.PENDING.toString());
        assertEquals("IN_PROGRESS", TransactionStatus.IN_PROGRESS.toString());
        assertEquals("COMPLETED", TransactionStatus.COMPLETED.toString());
        assertEquals("CANCELLED", TransactionStatus.CANCELLED.toString());
    }

    @Test
    void testOrdinal() {
        assertEquals(0, TransactionStatus.PENDING.ordinal());
        assertEquals(1, TransactionStatus.IN_PROGRESS.ordinal());
        assertEquals(2, TransactionStatus.COMPLETED.ordinal());
        assertEquals(3, TransactionStatus.CANCELLED.ordinal());
    }

    @Test
    void testEquality() {
        assertTrue(TransactionStatus.PENDING == TransactionStatus.PENDING);
        assertFalse(TransactionStatus.PENDING == TransactionStatus.COMPLETED);

        assertEquals(TransactionStatus.PENDING, TransactionStatus.PENDING);
        assertNotEquals(TransactionStatus.PENDING, TransactionStatus.COMPLETED);
    }

    @Test
    void testEnumSwitch() {
        for (TransactionStatus status : TransactionStatus.values()) {
            String result = getDisplayNameForStatus(status);

            switch (status) {
                case PENDING:
                    assertEquals("Waiting for confirmation", result);
                    break;
                case IN_PROGRESS:
                    assertEquals("Being processed", result);
                    break;
                case COMPLETED:
                    assertEquals("Successfully completed", result);
                    break;
                case CANCELLED:
                    assertEquals("Transaction cancelled", result);
                    break;
                default:
                    fail("Unexpected status value: " + status);
            }
        }
    }

    @Test
    void testEnumComparison() {
        assertTrue(TransactionStatus.PENDING.ordinal() < TransactionStatus.COMPLETED.ordinal());
        assertTrue(TransactionStatus.COMPLETED.ordinal() > TransactionStatus.IN_PROGRESS.ordinal());
        assertTrue(TransactionStatus.CANCELLED.ordinal() > TransactionStatus.COMPLETED.ordinal());
    }

    @Test
    void testSerializability() {
        assertTrue(java.io.Serializable.class.isAssignableFrom(TransactionStatus.class));

        for (TransactionStatus status : TransactionStatus.values()) {
            assertNotNull(status);
        }
    }

    private String getDisplayNameForStatus(TransactionStatus status) {
        switch (status) {
            case PENDING:
                return "Waiting for confirmation";
            case IN_PROGRESS:
                return "Being processed";
            case COMPLETED:
                return "Successfully completed";
            case CANCELLED:
                return "Transaction cancelled";
            default:
                return "Unknown status";
        }
    }
}