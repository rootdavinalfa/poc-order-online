package xyz.dvnlabs.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.dvnlabs.corelib.exception.ResourceExistException;
import xyz.dvnlabs.corelib.exception.ResourceNotFoundException;
import xyz.dvnlabs.payment.entity.Payment;
import xyz.dvnlabs.payment.repository.PaymentRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentService {
    private static final String SERVICE_ID_NAME = "PAYMENT REFERENCES";

    private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;

    public PaymentService(ObjectMapper objectMapper, PaymentRepository paymentRepository) {
        this.objectMapper = objectMapper;
        this.paymentRepository = paymentRepository;
    }

    public Page<Payment> getPage(Pageable pageable) {
        return paymentRepository.getPaymentWithQuery(pageable);
    }


    @Transactional(rollbackFor = RuntimeException.class)
    public Payment save(Payment payment) {
        if (payment.getId() != null && paymentRepository.existsById(payment.getId())) {
            throw new ResourceExistException(SERVICE_ID_NAME + " is exist");
        }

        return paymentRepository.save(payment);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public Payment update(Payment payment) {
        if (!paymentRepository.existsById(payment.getId())) {
            throw new ResourceNotFoundException(SERVICE_ID_NAME + " not found!");
        }

        return paymentRepository.save(payment);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void createPayment(Payment payment) {
        payment.setPaymentStatus("0");
        payment.setRequestedOn(LocalDateTime.now());
        save(payment);
    }

    @KafkaListener(topics = "orders_to_payment", groupId = "payment")
    @Transactional(rollbackFor = RuntimeException.class)
    public void subscribeOrdersToPayment(String payload) throws JsonProcessingException {
        log.info("Received message: " + payload);
        Payment payment = objectMapper.readValue(payload, Payment.class);
        createPayment(payment);

    }

}
