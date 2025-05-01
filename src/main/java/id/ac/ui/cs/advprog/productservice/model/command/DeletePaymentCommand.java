package id.ac.ui.cs.advprog.productservice.model.command;

import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;

public class DeletePaymentCommand implements PaymentCommand {

    private PaymentServiceImpl service;
    private String paymentId;

    public DeletePaymentCommand(PaymentServiceImpl service, String paymentId) {
        this.service = service;
        this.paymentId = paymentId;
    }

    @Override
    public void execute() {
        // TODO
    }
}