package xyz.dvnlabs.orders.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import xyz.dvnlabs.orders.entity.Orders;
import xyz.dvnlabs.orders.service.OrdersService;

import java.time.LocalDate;

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
            @RequestParam(required = false) LocalDate dateFrom,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(required = false) LocalDate dateTo
    ) {

        return ordersService.getOrderPage(pageable, dateFrom, dateTo);
    }

    @GetMapping("/{id}")
    public Orders findById(
            @PathVariable Long id
    ) {
        return ordersService.findById(id);
    }

    @PostMapping
    public void createOrder(
            @RequestBody Orders orders
    ) throws JsonProcessingException {
        ordersService.createOrders(orders);
    }

}
