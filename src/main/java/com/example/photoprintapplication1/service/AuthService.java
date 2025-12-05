package com.example.photoprintapplication.service;

import com.example.photoprintapplication.dto.RegisterRequest;
import com.example.photoprintapplication.models.User;
import com.example.photoprintapplication.models.Customer;
import com.example.photoprintapplication.models.Role;
import com.example.photoprintapplication.repository.UserRepository;
import com.example.photoprintapplication.repository.CustomerRepository;
import com.example.photoprintapplication.util.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public AuthService(UserRepository userRepository, CustomerRepository customerRepository,
                       PasswordEncoder passwordEncoder, PasswordValidator passwordValidator) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    @Transactional
    public String register(RegisterRequest request) {
        // Проверка существования пользователя
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Валидация пароля
        if (!passwordValidator.isValid(request.getPassword())) {
            throw new RuntimeException(passwordValidator.getPasswordRequirements());
        }

        // Создание пользователя
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );

        // Создание Customer для пользователя
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setUser(user);

        user.setCustomer(customer);

        userRepository.save(user);
        return "User registered successfully";
    }
}