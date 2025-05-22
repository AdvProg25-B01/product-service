// src/main/java/id/ac/ui/cs/advprog/productservice/productmanagement/model/Product.java

package id.ac.ui.cs.advprog.productservice.productmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // Add this if not already present
import org.hibernate.annotations.JdbcTypeCode; // Import this
import org.hibernate.type.SqlTypes; // Import this

import java.util.UUID;

@Entity
@Table(name = "\"product\"") // Keep the quotes for consistency/safety
@Getter @Setter
@NoArgsConstructor // Lombok annotation for no-arg constructor
public class Product {

    @Id
    // Use @JdbcTypeCode to explicitly map UUID to a character type in DB
    @JdbcTypeCode(SqlTypes.CHAR) // This is the key change!
    // No @GeneratedValue(strategy = GenerationType.UUID) needed anymore as you generate it manually
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String category; // If nullable, no @Column(nullable=false)

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private double price;

    public Product(String name, String category, int stock, double price) {
        this.id = UUID.randomUUID(); // Your existing UUID generation
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.price = price;
    }

    // You can remove your old @Id and @GeneratedValue lines if they are still there
    // and replace with the new @Id and @JdbcTypeCode line.
}