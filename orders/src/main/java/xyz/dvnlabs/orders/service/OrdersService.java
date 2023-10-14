package xyz.dvnlabs.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import xyz.dvnlabs.orders.dto.CustomerDTO;
import xyz.dvnlabs.orders.dto.PaymentDTO;
import xyz.dvnlabs.orders.entity.Orders;
import xyz.dvnlabs.orders.repository.OrdersRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrdersService {
    private static final String SERVICE_ID_NAME = "ORDER_REFERENCES";


    private final RestTemplate restTemplate;
    private final OrdersRepository ordersRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrdersService(RestTemplate restTemplate, OrdersRepository ordersRepository,
                         ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.ordersRepository = ordersRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Page<Orders> getOrderPage(
            Pageable pageable,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {

        if (dateFrom == null) {
            dateFrom = LocalDate.now();
        }

        if (dateTo == null) {
            dateTo = LocalDate.now().plusDays(7);
        }


        return ordersRepository.getOrderWithQuery(
                pageable, dateFrom, dateTo).map(orders -> {
                    try {
                        CustomerDTO customerDTO = restTemplate
                                .getForObject("http://CUSTOMER/customer/" + orders.getCustomerID(),
                                        CustomerDTO.class);
                        orders.setCustomerName(customerDTO != null ? customerDTO.getCustomerName() : "Customer");
                    } catch (RestClientException exception) {
                        exception.printStackTrace();
                    }

                    return orders;
                }
        );

    }

    @Transactional(rollbackFor = RuntimeException.class)
    public Orders save(Orders orders) {
        if (orders.getId() != null && ordersRepository.existsById(orders.getId())) {
            throw new RuntimeException(SERVICE_ID_NAME + " is exist");
        }
        orders.setTrxDate(LocalDateTime.now());
        return ordersRepository.save(orders);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public Orders update(Orders orders) {
        if (!ordersRepository.existsById(orders.getId())) {
            throw new RuntimeException(SERVICE_ID_NAME + " not found!");
        }

        return ordersRepository.save(orders);
    }

    @Transactional(
            rollbackFor = RuntimeException.class
    )
    public void createOrders(Orders orders) throws JsonProcessingException {
        orders.setTrxStatus("0");
        orders = save(orders);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setOrderID(orders.getId());
        String jsonPayment = objectMapper.writeValueAsString(paymentDTO);
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send("orders_to_payment", jsonPayment);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Unable to send message " + jsonPayment + " , Error: " + ex.getMessage());
            }
        });
    }

}
