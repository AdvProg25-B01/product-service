package id.ac.ui.cs.advprog.productservice.model.command;

import id.ac.ui.cs.advprog.productservice.model.Payment;
import id.ac.ui.cs.advprog.productservice.service.PaymentService;
import id.ac.ui.cs.advprog.productservice.service.PaymentServiceImpl;

public class CreatePaymentCommand implements PaymentCommand {

    private final PaymentServiceImpl service;
    private final Payment paymentToCreate;
    private Payment createdPaymentResult;

    public CreatePaymentCommand(PaymentServiceImpl service, Payment payment) {
        this.service = service;
        this.paymentToCreate = payment;
    }

    @Override
    public void execute() {
        this.createdPaymentResult = service.createPayment(this.paymentToCreate);
    }

    public Payment getResult() {
        return this.createdPaymentResult;
    }
}