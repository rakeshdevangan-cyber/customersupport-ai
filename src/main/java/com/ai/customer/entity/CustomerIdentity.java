package com.ai.customer.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "customer_identities",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_customer_identity_issuer_subject",
                columnNames = {"issuer", "subject"}
        )
)
public class CustomerIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String issuer;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(name = "customer_id", nullable = false, length = 100)
    private String customerId;

    protected CustomerIdentity() {}

    public CustomerIdentity(
            String issuer,
            String subject,
            String customerId) {
        this.issuer = issuer;
        this.subject = subject;
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }
}