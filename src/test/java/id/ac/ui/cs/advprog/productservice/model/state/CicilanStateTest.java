package id.ac.ui.cs.advprog.productservice.model.state;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CicilanStateTest {

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
    void testHandle_shouldPrintCicilanMessage() {
        CicilanState state = new CicilanState();
        state.handle();

        String output = outputStream.toString().trim();
        assertTrue(output.contains("Pembayaran dalam cicilan."));
    }

    @Test
    void testHandle_doesNotThrowException() {
        CicilanState state = new CicilanState();

        assertDoesNotThrow(state::handle);
    }
}
