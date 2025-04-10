package id.ac.ui.cs.advprog.productservice.model.state;

public class CicilanState implements PaymentState {
    @Override
    public void handle() {
        System.out.println("Pembayaran dalam cicilan.");
    }
}