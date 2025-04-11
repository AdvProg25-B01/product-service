package id.ac.ui.cs.advprog.productservice.model.command;

public class DeletePaymentCommand implements PaymentCommand {

    private PaymentService service;
    private Long paymentId;

    public DeletePaymentCommand(PaymentService service, Long paymentId) {
        this.service = service;
        this.paymentId = paymentId;
    }

    @Override
    public void execute() {
        // TODO
    }
}