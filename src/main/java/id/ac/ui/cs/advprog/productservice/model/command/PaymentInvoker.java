package id.ac.ui.cs.advprog.productservice.model.command;

public class PaymentInvoker {
    private PaymentCommand command;

    public void setCommand(PaymentCommand command) {
        this.command = command;
    }

    public void run() {
        command.execute();
    }
}