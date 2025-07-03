package com.invoice.Invoice_System.repository;

import com.invoice.Invoice_System.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    List<Invoice> findByCustomerId(Long customerId);
}
