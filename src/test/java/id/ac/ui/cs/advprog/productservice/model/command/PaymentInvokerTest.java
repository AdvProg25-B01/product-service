package id.ac.ui.cs.advprog.productservice.model.command;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class PaymentInvokerTest {

    @Test
    void testRunExecutesCommandSuccessfully() {
        // Arrange
        PaymentCommand command = mock(PaymentCommand.class);
        PaymentInvoker invoker = new PaymentInvoker();
        invoker.setCommand(command);

        // Act
        invoker.run();

        // Assert
        verify(command, times(1)).execute();
    }
    @Test
    void testRunWithoutSettingCommandThrowsException() {
        PaymentInvoker invoker = new PaymentInvoker();

        // Assert
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, invoker::run);
    }
}
