package id.ac.ui.cs.advprog.productservice.model.state;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class PaymentContextTest {

    @Test
    void testProcessWithValidState_callsHandleMethod() {
        PaymentState mockState = mock(PaymentState.class);
        PaymentContext context = new PaymentContext();

        context.setState(mockState);
        context.process();

        verify(mockState, times(1)).handle();
    }

    @Test
    void testProcessWithNullState_doesNotThrowException() {
        PaymentContext context = new PaymentContext();

        context.process();
    }
}

