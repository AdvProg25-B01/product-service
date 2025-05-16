package id.ac.ui.cs.advprog.productservice.model.command;

import lombok.Setter;

@Setter
public class PaymentInvoker {
    private PaymentCommand command;

    public void run() {
        command.execute();
    }
}