package id.ac.ui.cs.advprog.productservice.model.state;

public class PaymentContext {
    private PaymentState state;

    public void setState(PaymentState state) {
        this.state = state;
    }

    public void process() {
        if (state != null) {
            state.handle();
        }
    }
}