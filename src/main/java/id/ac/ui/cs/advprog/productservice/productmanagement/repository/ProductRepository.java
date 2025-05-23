package id.ac.ui.cs.advprog.productservice.productmanagement.repository;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByName(String name);
}