package id.ac.ui.cs.advprog.productservice.model.command;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreatePaymentCommandTest {

    private PaymentServiceImpl paymentService;
    private Payment payment;
    private CreatePaymentCommand command;

    @BeforeEach
    public void setUp() {
        paymentService = Mockito.mock(PaymentServiceImpl.class);
        payment = new Payment();
        payment.setId("p1");
        payment.setCustomerId("c1");
        payment.setAmount(100.0);
        payment.setMethod("OVO");
        payment.setStatus("PENDING");

        command = new CreatePaymentCommand(paymentService, payment);
    }

    @Test
    public void testExecute_HappyPath_ShouldCallCreatePayment() {
        command.execute();
        Mockito.verify(paymentService).createPayment(payment);
    }

    @Test
    public void testExecute_UnhappyPath_ShouldThrowRuntimeException() {
        Mockito.doThrow(new RuntimeException("Payment failed"))
                .when(paymentService).createPayment(payment);

        assertThrows(RuntimeException.class, () -> command.execute());
        Mockito.verify(paymentService).createPayment(payment);
    }
}
