package id.ac.ui.cs.advprog.productservice.productmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "\"product\"") 
@Getter @Setter
@NoArgsConstructor
public class Product {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private double price;
    
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID supplierId; // Added supplier reference

    public Product(String name, String category, int stock, double price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.price = price;
    }
}