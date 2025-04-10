package id.ac.ui.cs.advprog.productservice.model.command;

public class CreatePaymentCommand implements PaymentCommand {

    private PaymentService service;
    private Payment payment;

    public CreatePaymentCommand(PaymentService service, Payment payment) {
        this.service = service;
        this.payment = payment;
    }

    @Override
    public void execute() {
        // TODO
    }
}