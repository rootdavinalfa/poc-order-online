package xyz.dvnlabs.orders.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import xyz.dvnlabs.orders.entity.Orders;
import xyz.dvnlabs.orders.service.OrdersService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping("/page")
    public Page<Orders> getOrdersPage(
            Pageable pageable,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDateTime dateFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDateTime dateTo
    ) {

        return ordersService.getOrderPage(pageable, dateFrom, dateTo);
    }

    @PostMapping("/")
    public void createOrder(
            Orders orders
    ) {
        ordersService.createOrders(orders);
    }

}
