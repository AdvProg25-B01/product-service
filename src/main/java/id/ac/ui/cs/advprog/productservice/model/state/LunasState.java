package id.ac.ui.cs.advprog.productservice.model.state;

public class LunasState implements PaymentState {
    @Override
    public void handle() {
        System.out.println("Pembayaran lunas.");
    }
}
