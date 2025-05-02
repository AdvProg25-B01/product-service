package id.ac.ui.cs.advprog.productservice.model.command;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ViewPaymentHistoryCommandTest {

    private PaymentServiceImpl paymentService;
    private ViewPaymentHistoryCommand command;
    private String customerId = "cust123";

    @BeforeEach
    public void setUp() {
        paymentService = Mockito.mock(PaymentServiceImpl.class);
        command = new ViewPaymentHistoryCommand(paymentService, customerId);
    }

    @Test
    public void testExecute_HappyPath_ShouldCallGetPaymentsByCustomerId() {
        List<Payment> dummyList = Collections.singletonList(Mockito.mock(Payment.class));
        Mockito.when(paymentService.getPaymentsByCustomerId(customerId)).thenReturn(dummyList);

        command.execute();
        Mockito.verify(paymentService).getPaymentsByCustomerId(customerId);
    }

    @Test
    public void testExecute_UnhappyPath_ShouldThrowRuntimeException() {
        Mockito.when(paymentService.getPaymentsByCustomerId(customerId))
                .thenThrow(new RuntimeException("Customer ID not found"));

        assertThrows(RuntimeException.class, () -> command.execute());
        Mockito.verify(paymentService).getPaymentsByCustomerId(customerId);
    }
}
