package id.ac.ui.cs.advprog.productservice.model.command;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;

public class CreatePaymentCommand implements PaymentCommand {

    private PaymentServiceImpl service;
    private Payment payment;

    public CreatePaymentCommand(PaymentServiceImpl service, Payment payment) {
        this.service = service;
        this.payment = payment;
    }

    @Override
    public void execute() {
        // TODO
    }
}