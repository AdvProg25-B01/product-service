package id.ac.ui.cs.advprog.productservice.productmanagement.service;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import id.ac.ui.cs.advprog.productservice.productmanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public boolean addProduct(Product product, boolean confirmed) {
        if (!confirmed || product.getPrice() < 0) {
            return false;
        }
        repository.save(product);
        return true;
    }

    public boolean editProduct(Product updatedProduct, boolean confirmed) {
        if (!confirmed) {
            return false;
        }
        repository.update(updatedProduct);
        return true;
    }

    public boolean deleteProduct(String name, boolean confirmed) {
        if (!confirmed) {
            return false;
        }
        repository.delete(name);
        return true;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }
}