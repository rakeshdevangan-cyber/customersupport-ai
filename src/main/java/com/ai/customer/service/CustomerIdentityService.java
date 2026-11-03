package com.ai.customer.service;

import com.ai.customer.entity.CustomerIdentity;
import com.ai.customer.exception.UserNotFoundException;
import com.ai.customer.repository.CustomerIdentityRepository;
import org.springframework.stereotype.Service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;


@Service
public class CustomerIdentityService {

    private final CustomerIdentityRepository repository;

    public CustomerIdentityService(
            CustomerIdentityRepository repository) {
        this.repository = repository;
    }

    public String resolveCustomerId(Jwt jwt)  {

        String customerId = jwt.getSubject();
        String issuer = jwt.getIssuer().toString();

        //ToDo decrypt customerId from jwt

        return repository.findByIssuerAndSubject(issuer, customerId)
                .map(CustomerIdentity::getCustomerId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Customer identity not registered"
                        ));
    }
}
