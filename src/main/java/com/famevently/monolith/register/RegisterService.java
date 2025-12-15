package com.famevently.monolith.register;

import com.famevently.monolith.customer.Customer;
import com.famevently.monolith.customer.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegisterService {
     private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer register(RegisterRequest request){

        if (customerRepository.getCustomer(request.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already used");
        }

        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setLanguage(request.getLanguage());
        customer.setGender(request.getGender());
        customer.setBirthday(request.getBirthday());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        customer.setPwdHash(hashedPassword);

        customerRepository.save(customer);
        return customer;
    }
}
