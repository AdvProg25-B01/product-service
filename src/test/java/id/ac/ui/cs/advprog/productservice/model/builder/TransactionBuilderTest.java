package id.ac.ui.cs.advprog.productservice.model.builder;

import id.ac.ui.cs.advprog.productservice.model.Product;
import id.ac.ui.cs.advprog.productservice.model.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionBuilderTest {

    @Test
    void shouldBuildTransactionWithMultipleProducts() {
        Product p1 = new Product("P01", "Mouse", 50_000);
        Product p2 = new Product("P02", "Keyboard", 150_000);

        Transaction transaction = new TransactionBuilder()
                .withId("T01")
                .withProduct(p1)
                .withProduct(p2)
                .build();

        assertEquals("T01", transaction.getId());
        assertEquals(2, transaction.getProducts().size());
        assertEquals(200_000, transaction.getTotalAmount());
    }
}
