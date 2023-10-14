package xyz.dvnlabs.customer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.dvnlabs.corelib.exception.ResourceExistException;
import xyz.dvnlabs.corelib.exception.ResourceNotFoundException;
import xyz.dvnlabs.customer.entity.Customer;
import xyz.dvnlabs.customer.repository.CustomerRepository;

@Service
public class CustomerService {
    private static final String SERVICE_ID_NAME = "CUSTOMER";

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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


}
