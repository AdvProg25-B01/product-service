package id.ac.ui.cs.advprog.productservice.productmanagement.repository;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {
    private final List<Product> products = new ArrayList<>();

    public Product save(Product product) {
        products.add(product);
        return product;
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Optional<Product> findByName(String name) {
        return products.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }

    public Product findById(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    public void update(Product updatedProduct) {
        findByName(updatedProduct.getName()).ifPresent(p -> {
            p.setCategory(updatedProduct.getCategory());
            p.setStock(updatedProduct.getStock());
            p.setPrice(updatedProduct.getPrice());
        });
    }

    public void delete(String name) {
        findByName(name).ifPresent(products::remove);
    }

    public void clear() {
        products.clear(); // untuk membantu testing
    }
}