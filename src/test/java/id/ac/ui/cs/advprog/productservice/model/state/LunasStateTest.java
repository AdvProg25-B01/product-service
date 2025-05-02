package id.ac.ui.cs.advprog.productservice.model.state;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LunasStateTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testHandle_shouldPrintLunasMessage() {
        LunasState state = new LunasState();
        state.handle();

        String output = outputStream.toString().trim();
        assertTrue(output.contains("Pembayaran lunas."));
    }

    @Test
    void testHandle_doesNotThrowException() {
        LunasState state = new LunasState();

        assertDoesNotThrow(state::handle);
    }
}
