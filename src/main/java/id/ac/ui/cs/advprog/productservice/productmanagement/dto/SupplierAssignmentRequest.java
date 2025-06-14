package id.ac.ui.cs.advprog.productservice.productmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierAssignmentRequest {
    private UUID supplierId;
}