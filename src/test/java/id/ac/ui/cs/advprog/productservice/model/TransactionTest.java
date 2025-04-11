package id.ac.ui.cs.advprog.productservice.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldReturnCorrectTotalAmount() {
        Product p1 = new Product("P001", "Pulpen", 3000);
        Product p2 = new Product("P002", "Buku", 10000);

        Transaction transaction = new Transaction("T123", List.of(p1, p2));

        assertEquals("T123", transaction.getId());
        assertEquals(2, transaction.getProducts().size());
        assertEquals(13000, transaction.getTotalAmount());
    }

    @Test
    void shouldReturnZeroIfNoProduct() {
        Transaction transaction = new Transaction("T124", List.of());

        assertEquals("T124", transaction.getId());
        assertEquals(0, transaction.getProducts().size());
        assertEquals(0, transaction.getTotalAmount());
    }
}