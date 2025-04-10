package id.ac.ui.cs.advprog.productservice.model.command;

public class ViewPaymentHistoryCommand implements PaymentCommand {

    private final PaymentService service;
    private final Long customerId;

    public ViewPaymentHistoryCommand(PaymentService service, Long customerId) {
        this.service = service;
        this.customerId = customerId;
    }

    @Override
    public void execute() {
        // TODO
    }
}