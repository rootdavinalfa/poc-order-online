package xyz.dvnlabs.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import xyz.dvnlabs.corelib.exception.ResourceExistException;
import xyz.dvnlabs.corelib.exception.ResourceNotFoundException;
import xyz.dvnlabs.payment.dto.OrdersDTO;
import xyz.dvnlabs.payment.dto.PaymentCustomerDTO;
import xyz.dvnlabs.payment.entity.Payment;
import xyz.dvnlabs.payment.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PaymentService {
    private static final String SERVICE_ID_NAME = "PAYMENT REFERENCES";

    private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;

    public PaymentService(ObjectMapper objectMapper, PaymentRepository paymentRepository,
                          KafkaTemplate<String, String> kafkaTemplate, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    public Page<Payment> getPage(Pageable pageable) {
        return paymentRepository.getPaymentWithQuery(pageable);
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SERVICE_ID_NAME + " is not found!"));
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
    public void createPayment(Payment payment) throws JsonProcessingException {
        payment.setPaymentStatus("0");
        payment.setRequestedOn(LocalDateTime.now());
        payment = save(payment);

        PaymentCustomerDTO paymentCustomerDTO = new PaymentCustomerDTO();
        paymentCustomerDTO.setId(payment.getId());

        // Get customer from order
        OrdersDTO ordersDTO = restTemplate
                .getForObject("http://ORDERS/orders/" + payment.getOrderID(),
                        OrdersDTO.class);

        if (ordersDTO == null){
            throw new ResourceNotFoundException("Orders not found");
        }

        paymentCustomerDTO.setCustomerID(ordersDTO.getCustomerID());


        paymentCustomerDTO.setPaymentStatus(payment.getPaymentStatus());
        paymentCustomerDTO.setOrderID(payment.getOrderID());
        paymentCustomerDTO.setRequestedOn(payment.getRequestedOn());

        String jsonPayment = objectMapper.writeValueAsString(paymentCustomerDTO);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("payment_to_customer", jsonPayment);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Unable to send message " + jsonPayment + " , Error: " + ex.getMessage());
            }
        });


    }

    @KafkaListener(topics = "orders_to_payment", groupId = "payment")
    @Transactional(rollbackFor = RuntimeException.class)
    public void subscribeOrdersToPayment(String payload) throws JsonProcessingException {
        log.info("Received message: " + payload);
        Payment payment = objectMapper.readValue(payload, Payment.class);
        createPayment(payment);

    }

    @KafkaListener(topics = "customer_to_payment", groupId = "payment")
    @Transactional(rollbackFor = RuntimeException.class)
    public void subscribeCustomerToPayment(String payload) throws JsonProcessingException {
        log.info("Received message: " + payload);
        PaymentCustomerDTO paymentCustomerDTO = objectMapper.readValue(payload, PaymentCustomerDTO.class);

        Payment payment = findById(paymentCustomerDTO.getId());
        payment.setPaidOn(paymentCustomerDTO.getPaidOn());
        payment.setPaymentStatus(paymentCustomerDTO.getPaymentStatus());
        // Update payment
        update(payment);

    }

}
