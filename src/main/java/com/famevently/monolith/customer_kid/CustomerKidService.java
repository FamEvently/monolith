package com.famevently.monolith.customer_kid;

import org.springframework.stereotype.Service;

@Service
public class CustomerKidService {
    private final CustomerKidRepository repository;

    public CustomerKidService(CustomerKidRepository repository) {
        this.repository = repository;
    }

    public void addKid(AddKidRequest request){
        CustomerKid kid = new CustomerKid();

        kid.setCustomerId(request.getCustomerId());
        kid.setFirstName(request.getFirstName());
        kid.setLastName(request.getLastName());
        kid.setBirthday(request.getBirthday());
        kid.setGender(request.getGender());
        kid.setDescription(request.getDescription());

        repository.save(kid);
    }
}
