package id.ac.ui.cs.advprog.productservice.productmanagement.service;

import id.ac.ui.cs.advprog.productservice.productmanagement.factory.ProductFactory;
import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public boolean addProduct(Product product, boolean confirmed) {
        if (!confirmed || product.getPrice() <= 0) {
            return false;
        }
        repository.save(product);
        return true;
    }

    public boolean editProduct(Product updatedProduct, boolean confirmed) {
        if (!confirmed) {
            return false;
        }

        Optional<Product> existing = repository.findById(updatedProduct.getId());
        if (existing.isEmpty()) return false;

        Product product = existing.get();
        product.setCategory(updatedProduct.getCategory());
        product.setStock(updatedProduct.getStock());
        product.setPrice(updatedProduct.getPrice());

        repository.save(product);
        return true;
    }

    public boolean deleteProduct(String name, boolean confirmed) {
        if (!confirmed) {
            return false;
        }

        Optional<Product> productToDelete = repository.findByName(name);
        if (productToDelete.isPresent()) {
            repository.delete(productToDelete.get());
            return true;
        }
        return false;
    }

    public Optional<Product> getProductById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return repository.findById(uuid);
        } catch (IllegalArgumentException e) {
            // Invalid UUID format
            return Optional.empty();
        }
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }
}