package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Customer;
import com.example.photoprintapplication.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @GetMapping
    public List<Customer> all() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @RequestBody Customer customer) {
        Customer exist = customerRepository.findById(id).orElse(null);
        if (exist == null) return null;

        exist.setFirstName(customer.getFirstName());
        exist.setLastName(customer.getLastName());
        exist.setPhone(customer.getPhone());
        exist.setEmail(customer.getEmail());

        return customerRepository.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return "ok";
    }
}