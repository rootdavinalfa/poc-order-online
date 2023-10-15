package xyz.dvnlabs.customer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xyz.dvnlabs.customer.dto.PayDTO;
import xyz.dvnlabs.customer.entity.Customer;
import xyz.dvnlabs.customer.entity.CustomerPaymentHistory;
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

    @PostMapping("/pay")
    public void pay(
            @RequestBody PayDTO payDTO
    ) throws JsonProcessingException {
        customerService.pay(payDTO);
    }

    @GetMapping("/ack/page")
    public Page<CustomerPaymentHistory> getPage(
            Pageable pageable,
            @RequestParam String customerID,
            @RequestParam(defaultValue = "") String customerAck
    ) {
        return customerService.getPagePaymentHistory(
                pageable, customerID, customerAck);
    }

}
