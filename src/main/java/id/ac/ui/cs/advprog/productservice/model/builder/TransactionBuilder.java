package id.ac.ui.cs.advprog.productservice.model.builder;

import id.ac.ui.cs.advprog.productservice.model.Transaction;
import id.ac.ui.cs.advprog.productservice.model.TransactionItem;
import id.ac.ui.cs.advprog.productservice.enums.TransactionStatus;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionBuilder {
    private String id;
    private String customerId;
    private List<TransactionItem> items = new ArrayList<>();
    private String paymentMethod;
    private TransactionStatus status;

    public TransactionBuilder() {
        this.id = UUID.randomUUID().toString();
        this.status = TransactionStatus.PENDING;
    }

    public TransactionBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public TransactionBuilder withCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public TransactionBuilder withItem(Product product, int quantity) {
        this.items.add(new TransactionItem(product, quantity));
        return this;
    }

    public TransactionBuilder withItems(List<TransactionItem> items) {
        this.items.addAll(items);
        return this;
    }

    public TransactionBuilder withPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public TransactionBuilder withStatus(TransactionStatus status) {
        this.status = status;
        return this;
    }

    public Transaction build() {
        Transaction transaction = new Transaction(id, customerId, items, paymentMethod, status);
        transaction.calculateTotalAmount();
        return transaction;
    }
}