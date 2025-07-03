package com.invoice.Invoice_System.repository;

import com.invoice.Invoice_System.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    public Optional<Customer> findByEmail(String email);
}
