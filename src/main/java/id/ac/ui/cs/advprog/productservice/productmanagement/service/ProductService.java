package id.ac.ui.cs.advprog.productservice.productmanagement.service;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.repository.ProductRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<Boolean> addProduct(Product product, boolean confirmed) {
        if (!confirmed || product.getPrice() <= 0) {
            return CompletableFuture.completedFuture(false);
        }
        repository.save(product);
        return CompletableFuture.completedFuture(true);
    }

    @Async
    public CompletableFuture<Boolean> editProduct(Product updatedProduct, boolean confirmed) {
        if (!confirmed) {
            return CompletableFuture.completedFuture(false);
        }

        Optional<Product> existing = repository.findById(updatedProduct.getId());
        if (existing.isEmpty()) return CompletableFuture.completedFuture(false);

        Product product = existing.get();
        product.setCategory(updatedProduct.getCategory());
        product.setStock(updatedProduct.getStock());
        product.setPrice(updatedProduct.getPrice());

        repository.save(product);
        return CompletableFuture.completedFuture(true);
    }

    @Async
    public CompletableFuture<Boolean> deleteProduct(String name, boolean confirmed) {
        if (!confirmed) {
            return CompletableFuture.completedFuture(false);
        }

        Optional<Product> productToDelete = repository.findByName(name);
        if (productToDelete.isPresent()) {
            repository.delete(productToDelete.get());
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.completedFuture(false);
    }

    public Optional<Product> getProductById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return repository.findById(uuid);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }
    
    @Async
    public CompletableFuture<Boolean> assignSupplierToProduct(UUID productId, UUID supplierId) {
        Optional<Product> productOpt = repository.findById(productId);
        if (productOpt.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        
        Product product = productOpt.get();
        product.setSupplierId(supplierId);
        repository.save(product);
        
        return CompletableFuture.completedFuture(true);
    }
}