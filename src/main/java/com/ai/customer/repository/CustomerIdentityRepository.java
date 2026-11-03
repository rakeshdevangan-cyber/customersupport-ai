package com.ai.customer.repository;

import com.ai.customer.entity.CustomerIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerIdentityRepository
        extends JpaRepository<CustomerIdentity, Long> {

    Optional<CustomerIdentity> findByIssuerAndSubject(
            String issuer,
            String subject);
}