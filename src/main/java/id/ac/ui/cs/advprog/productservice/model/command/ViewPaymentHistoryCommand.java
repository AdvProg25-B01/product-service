package id.ac.ui.cs.advprog.productservice.model.command;
import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;
import lombok.Getter;

import java.util.List;

public class ViewPaymentHistoryCommand implements PaymentCommand {

    private final PaymentServiceImpl service;
    private final String customerId;
    @Getter
    private List<Payment> result;

    public ViewPaymentHistoryCommand(PaymentServiceImpl service, String customerId) {
        this.service = service;
        this.customerId = customerId;
    }

    @Override
    public void execute() {
        this.result = service.getPaymentsByCustomerId(customerId);
    }

}
