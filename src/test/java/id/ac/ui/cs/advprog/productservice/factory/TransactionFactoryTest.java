package id.ac.ui.cs.advprog.productservice.factory;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.model.Transaction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFactoryTest {

    @Test
    void shouldCreateTransactionFromFactory() {
        Product p1 = new Product("Monitor", "Electronics", 500_000);
        Product p2 = new Product("HDMI Cable", "Accessories", 100_000);

        Transaction transaction = TransactionFactory.create("T02", List.of(p1, p2));

        assertNotNull(transaction);
        assertEquals("T02", transaction.getId());
        assertEquals(600_000, transaction.getTotalAmount());
    }
}