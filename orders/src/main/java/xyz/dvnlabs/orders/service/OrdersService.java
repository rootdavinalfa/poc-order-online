package xyz.dvnlabs.orders.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import xyz.dvnlabs.orders.dto.CustomerDTO;
import xyz.dvnlabs.orders.entity.Orders;
import xyz.dvnlabs.orders.repository.OrdersRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class OrdersService {
    private static final String SERVICE_ID_NAME = "ORDER_REFERENCES";


    private final RestTemplate restTemplate;
    private final OrdersRepository ordersRepository;

    public OrdersService(RestTemplate restTemplate, OrdersRepository ordersRepository) {
        this.restTemplate = restTemplate;
        this.ordersRepository = ordersRepository;
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
    public void createOrders(Orders orders) {
        orders.setTrxStatus("0");
        save(orders);
        // TODO: Publish to kafka
    }

}
