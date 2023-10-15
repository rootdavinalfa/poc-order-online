package xyz.dvnlabs.customer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import xyz.dvnlabs.corelib.exception.ResourceExistException;
import xyz.dvnlabs.corelib.exception.ResourceNotFoundException;
import xyz.dvnlabs.customer.dto.OrdersDTO;
import xyz.dvnlabs.customer.dto.PayDTO;
import xyz.dvnlabs.customer.dto.PaymentCustomerDTO;
import xyz.dvnlabs.customer.entity.Customer;
import xyz.dvnlabs.customer.entity.CustomerPaymentHistory;
import xyz.dvnlabs.customer.repository.CustomerPaymentHistoryRepository;
import xyz.dvnlabs.customer.repository.CustomerRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CustomerService {
    private static final String SERVICE_ID_NAME = "CUSTOMER";
    private static final String SERVICE_HISTORY = "CUSTOMER PAYMENT HISTORY";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CustomerRepository customerRepository;
    private final CustomerPaymentHistoryRepository customerPaymentHistoryRepository;

    public CustomerService(RestTemplate restTemplate, ObjectMapper objectMapper,
                           KafkaTemplate<String, String> kafkaTemplate, CustomerRepository customerRepository,
                           CustomerPaymentHistoryRepository customerPaymentHistoryRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.customerRepository = customerRepository;
        this.customerPaymentHistoryRepository = customerPaymentHistoryRepository;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public Customer save(Customer customer) {
        if (customer.getId() != null && customerRepository.existsById(customer.getId())) {
            throw new ResourceExistException(SERVICE_ID_NAME + " is exist");
        }

        return customerRepository.save(customer);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public Customer update(Customer customer) {
        if (!customerRepository.existsById(customer.getId())) {
            throw new ResourceNotFoundException(SERVICE_ID_NAME + " not found!");
        }

        return customerRepository.save(customer);
    }

    public Customer findById(Long customerID) {
        return customerRepository.findById(customerID)
                .orElseThrow(() -> new ResourceNotFoundException(SERVICE_ID_NAME + " not found!"));
    }

    public CustomerPaymentHistory getPaymentHistory(Long customerID, Long paymentID) {
        return new CustomerPaymentHistory();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public CustomerPaymentHistory save(CustomerPaymentHistory customerPaymentHistory) {
        if (customerPaymentHistory.getId() != null &&
                customerPaymentHistoryRepository.existsById(customerPaymentHistory.getId())) {
            throw new ResourceExistException(SERVICE_HISTORY + " is exist");
        }

        return customerPaymentHistoryRepository.save(customerPaymentHistory);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public CustomerPaymentHistory update(CustomerPaymentHistory customerPaymentHistory) {
        if (!customerPaymentHistoryRepository.existsById(customerPaymentHistory.getId())) {
            throw new ResourceNotFoundException(SERVICE_ID_NAME + " not found!");
        }

        return customerPaymentHistoryRepository.save(customerPaymentHistory);
    }

    public Page<CustomerPaymentHistory> getPagePaymentHistory(
            Pageable pageable,
            String customerID
    ) {
        return customerPaymentHistoryRepository.getPageWithQuery(
                pageable, customerID).map(customerPaymentHistory -> {
            try {
                OrdersDTO ordersDTO = restTemplate
                        .getForObject("http://ORDERS/orders/" + customerPaymentHistory.getOrdersID(),
                                OrdersDTO.class);
                if (ordersDTO != null) {
                    customerPaymentHistory.setOrders(ordersDTO);
                }
            } catch (RestClientException restClientException) {
                log.error(restClientException.getMessage());
            }
            return customerPaymentHistory;
        });
    }

    @KafkaListener(topics = "payment_to_customer", groupId = "customer")
    @Transactional(rollbackFor = RuntimeException.class)
    public void subscribePaymentToCustomer(String payload) throws JsonProcessingException {
        log.info("Received message: " + payload);
        PaymentCustomerDTO paymentCustomerDTO =
                objectMapper.readValue(payload, PaymentCustomerDTO.class);

        CustomerPaymentHistory customerPaymentHistory = new CustomerPaymentHistory();
        customerPaymentHistory.setPaymentID(paymentCustomerDTO.getId());
        customerPaymentHistory.setCustomerID(paymentCustomerDTO.getCustomerID());
        customerPaymentHistory.setOrdersID(paymentCustomerDTO.getOrderID());
        customerPaymentHistory.setCustomerAck("0");
        customerPaymentHistory.setCreatedOn(LocalDateTime.now());

        OrdersDTO ordersDTO = restTemplate
                .getForObject("http://ORDERS/orders/" + paymentCustomerDTO.getOrderID(),
                        OrdersDTO.class);
        if (ordersDTO != null) {
            customerPaymentHistory.setAmount(
                    ordersDTO.getTrxAmount() == null ? BigDecimal.ZERO : ordersDTO.getTrxAmount());
        } else {
            throw new ResourceNotFoundException("Order not found!");
        }


        save(customerPaymentHistory);

    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void pay(PayDTO payDTO) throws JsonProcessingException {
        CustomerPaymentHistory customerPaymentHistory =
                customerPaymentHistoryRepository.getHistoryWithQuery(
                        payDTO.customerID(),
                        payDTO.paymentID()
                ).orElseThrow(() -> new ResourceNotFoundException(SERVICE_HISTORY + " is not found"));
        customerPaymentHistory.setCustomerAck("1");
        Customer customer = findById(payDTO.customerID());

        BigDecimal finalBalance = customer
                .getBalance().subtract(customerPaymentHistory.getAmount(), new MathContext(23));

        customer.setBalance(finalBalance);
        // Update CustomerPaymentHistory
        update(customerPaymentHistory);
        // Update Customer
        update(customer);

        PaymentCustomerDTO paymentCustomerDTO = new PaymentCustomerDTO();
        paymentCustomerDTO.setId(payDTO.paymentID());
        paymentCustomerDTO.setPaidOn(LocalDateTime.now());
        paymentCustomerDTO.setPaymentStatus("1");

        String jsonPayment = objectMapper.writeValueAsString(paymentCustomerDTO);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("customer_to_payment", jsonPayment);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Unable to send message " + jsonPayment + " , Error: " + ex.getMessage());
            }
        });

    }


}
