package id.ac.ui.cs.advprog.productservice.model.command;

import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeletePaymentCommandTest {

    private PaymentServiceImpl paymentService;
    private DeletePaymentCommand command;
    private String paymentId = "p123";

    @BeforeEach
    public void setUp() {
        paymentService = Mockito.mock(PaymentServiceImpl.class);
        command = new DeletePaymentCommand(paymentService, paymentId);
    }

    @Test
    public void testExecute_HappyPath_ShouldCallDeletePayment() {
        command.execute();
        Mockito.verify(paymentService).deletePayment(paymentId);
    }

    @Test
    public void testExecute_UnhappyPath_ShouldThrowRuntimeException() {
        Mockito.doThrow(new RuntimeException("Payment not found"))
                .when(paymentService).deletePayment(paymentId);

        assertThrows(RuntimeException.class, () -> command.execute());
        Mockito.verify(paymentService).deletePayment(paymentId);
    }
}
