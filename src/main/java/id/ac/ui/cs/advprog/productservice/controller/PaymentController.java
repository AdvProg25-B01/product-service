package id.ac.ui.cs.advprog.productservice.controller;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;
import id.ac.ui.cs.advprog.productservice.model.command.CreatePaymentCommand;
import id.ac.ui.cs.advprog.productservice.model.command.DeletePaymentCommand;
import id.ac.ui.cs.advprog.productservice.model.command.ViewPaymentHistoryCommand;
import id.ac.ui.cs.advprog.productservice.model.command.PaymentCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    @PostMapping
     public ResponseEntity<Void> createPayment(@RequestBody Payment payment) {
         CreatePaymentCommand command = new CreatePaymentCommand(paymentService, payment);
         command.execute();
         Payment createdPayment = command.getResult();

         if (createdPayment == null || createdPayment.getId() == null) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
         }

         URI location = URI.create("/payments/" + createdPayment.getId());
         return ResponseEntity.created(location).build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getPaymentsByCustomerId(@PathVariable String customerId) {
        ViewPaymentHistoryCommand command = new ViewPaymentHistoryCommand(paymentService, customerId);
        command.execute();
        return ResponseEntity.ok(command.getResult());
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable String paymentId,
            @RequestParam String status
    ) {
        try {
            if (!status.equals("LUNAS") && !status.equals("CICILAN")) {
                return ResponseEntity.badRequest().body("Invalid status");
            }
            paymentService.updatePaymentStatus(paymentId, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException("Update failed", e);
        }
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable String paymentId) {
        try {
            PaymentCommand command = new DeletePaymentCommand(paymentService, paymentId);
            command.execute();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable String paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidJson(HttpMessageNotReadableException ex) {
        return "Malformed JSON request";
    }
}