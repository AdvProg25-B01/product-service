package id.ac.ui.cs.advprog.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreatePayment() throws Exception {
        Payment payment = new Payment();
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreatePayment_InvalidJson() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalidJson}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPaymentsByCustomerId() throws Exception {
        List<Payment> mockPayments = Collections.singletonList(new Payment());
        Mockito.when(paymentService.getPaymentsByCustomerId("123")).thenReturn(mockPayments);

        mockMvc.perform(get("/payments/customer/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetPaymentsByCustomerId_NotFound() throws Exception {
        Mockito.when(paymentService.getPaymentsByCustomerId("123"))
                .thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(get("/payments/customer/123"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdatePaymentStatus() throws Exception {
        mockMvc.perform(put("/payments/abc123/status")
                        .param("status", "LUNAS"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePaymentStatus_Invalid() throws Exception {
        Mockito.doThrow(new RuntimeException("Update failed"))
                .when(paymentService).updatePaymentStatus(eq("abc123"), eq("invalid"));

        mockMvc.perform(put("/payments/abc123/status")
                        .param("status", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeletePayment() throws Exception {
        mockMvc.perform(delete("/payments/abc123"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePayment_failed() throws Exception {
        String paymentId = "abc123";

        Mockito.doThrow(new RuntimeException("Delete failed"))
                .when(paymentService).deletePayment(paymentId);

        mockMvc.perform(delete("/payments/" + paymentId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetPaymentById() throws Exception {
        Payment payment = new Payment();
        payment.setId("abc123");
        Mockito.when(paymentService.getPaymentById("abc123")).thenReturn(payment);

        mockMvc.perform(get("/payments/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc123"));
    }

    @Test
    void testGetPaymentById_NotFound() throws Exception {
        Mockito.when(paymentService.getPaymentById("notFound"))
                .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/payments/notFound"))
                .andExpect(status().isInternalServerError());
    }
}
