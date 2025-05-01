package id.ac.ui.cs.advprog.productservice.model.command;
import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;

public class ViewPaymentHistoryCommand implements PaymentCommand {

    private final PaymentServiceImpl service;
    private final String customerId;

    public ViewPaymentHistoryCommand(PaymentServiceImpl service, String customerId) {
        this.service = service;
        this.customerId = customerId;
    }

    @Override
    public void execute() {
        // TODO
    }
}