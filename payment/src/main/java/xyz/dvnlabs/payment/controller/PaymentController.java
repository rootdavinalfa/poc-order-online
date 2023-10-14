package xyz.dvnlabs.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.dvnlabs.payment.entity.Payment;
import xyz.dvnlabs.payment.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/page")
    public Page<Payment> getPage(
            Pageable pageable
    ) {
        return paymentService.getPage(pageable);
    }

}
