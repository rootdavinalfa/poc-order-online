package xyz.dvnlabs.customer.controller;

import org.springframework.web.bind.annotation.*;
import xyz.dvnlabs.customer.entity.Customer;
import xyz.dvnlabs.customer.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public Customer findById(
            @PathVariable Long id
    ) {
        return customerService.findById(id);
    }

    @PostMapping
    public void create(
            @RequestBody Customer customer
    ) {
        customerService.save(customer);
    }

}
