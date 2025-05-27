package id.ac.ui.cs.advprog.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.productservice.config.TestSecurityConfig;
import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(PaymentController.class)
@Import(TestSecurityConfig.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentServiceImpl paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreatePayment() throws Exception {
        Payment paymentInput = new Payment();
        paymentInput.setId(null);


        Payment paymentOutput = new Payment();
        String generatedId = UUID.randomUUID().toString();
        paymentOutput.setId(generatedId);
        paymentOutput.setCreatedAt(new Date());

        when(paymentService.createPayment(any(Payment.class))).thenReturn(paymentOutput);


        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(paymentInput)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/payments/" + generatedId));
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
        when(paymentService.getPaymentsByCustomerId("123")).thenReturn(mockPayments);

        mockMvc.perform(get("/payments/customer/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetPaymentsByCustomerId_NotFound() throws Exception {
        when(paymentService.getPaymentsByCustomerId("123"))
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
        when(paymentService.getPaymentById("abc123")).thenReturn(payment);

        mockMvc.perform(get("/payments/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc123"));
    }

    @Test
    void testGetPaymentById_NotFound() throws Exception {
        when(paymentService.getPaymentById("notFound"))
                .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/payments/notFound"))
                .andExpect(status().isInternalServerError());
    }
}
