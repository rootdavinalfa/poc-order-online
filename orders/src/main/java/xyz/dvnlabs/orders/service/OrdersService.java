package xyz.dvnlabs.orders.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.dvnlabs.orders.entity.Orders;
import xyz.dvnlabs.orders.repository.OrdersRepository;

import java.time.LocalDateTime;

@Service
public class OrdersService {
    private static final String SERVICE_ID_NAME = "ORDER_REFERENCES";

    private final OrdersRepository ordersRepository;

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public Page<Orders> getOrderPage(
            Pageable pageable,
            LocalDateTime dateFrom,
            LocalDateTime dateTo
    ) {

        if (dateFrom == null) {
            dateFrom = LocalDateTime.now();
        }

        if (dateTo == null) {
            dateTo = LocalDateTime.now().plusDays(7);
        }

        return ordersRepository.getOrderWithQuery(
                pageable, dateFrom, dateTo);

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
        save(orders);
        // TODO: Publish to kafka
    }

}
