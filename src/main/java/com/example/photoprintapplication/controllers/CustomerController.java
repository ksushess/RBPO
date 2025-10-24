package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Customer;
import com.example.photoprintapplication.repository.PhotoPrintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private PhotoPrintRepository repo;

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return repo.save(customer);
    }

    @GetMapping
    public List<Customer> all() {
        return repo.findAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return repo.findCustomerById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @RequestBody Customer customer) {
        Customer exist = repo.findCustomerById(id).orElse(null);
        if (exist == null) return null;

        exist.setFirstName(customer.getFirstName());
        exist.setLastName(customer.getLastName());
        exist.setPhone(customer.getPhone());
        exist.setEmail(customer.getEmail());

        return repo.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteCustomerById(id);
        return "ok";
    }
}