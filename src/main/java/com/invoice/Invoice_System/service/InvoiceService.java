package com.invoice.Invoice_System.service;

import com.invoice.Invoice_System.exception.InvoiceNotFoundException;
import com.invoice.Invoice_System.model.Customer;
import com.invoice.Invoice_System.model.Invoice;
import com.invoice.Invoice_System.model.InvoiceItem;
import com.invoice.Invoice_System.repository.CustomerRepository;
import com.invoice.Invoice_System.repository.InvoiceRepository;
import com.invoice.Invoice_System.exception.CustomerNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final PdfGeneratorService pdfGeneratorService;

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerRepository customerRepository,PdfGeneratorService pdfGeneratorService) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.pdfGeneratorService= pdfGeneratorService;
    }

    public Invoice createInvoice(Long customerId, Invoice invoice) {

        System.out.println("=== Logging Invoice Items ===");
        for (InvoiceItem item : invoice.getInvoiceItems()) {
            System.out.println("Product Name: " + item.getProductName());
            System.out.println("Price: " + item.getPrice());
            System.out.println("Quantity: " + item.getQuantity());
            System.out.println("Discount: " + item.getDiscount());
            System.out.println("-----------------------------");
        }


        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
        invoice.setCustomer(customer);
        invoice.setDate(LocalDate.now());

        if (invoice.getInvoiceItems() == null || invoice.getInvoiceItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must contain at least one item.");
        }


        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;

        for (InvoiceItem item : invoice.getInvoiceItems()) {
            item.setInvoice(invoice);
            BigDecimal price = item.getPrice();
            int quantity = item.getQuantity();
            BigDecimal discount = item.getDiscount();

            BigDecimal quantityBD = new BigDecimal(quantity);

            BigDecimal total = price.multiply(quantityBD).subtract(discount);
            item.setFinalPrice(total);

            totalInvoiceAmount = totalInvoiceAmount.add(total);
        }

        invoice.setTotalAmount(totalInvoiceAmount);

        // 3. Save invoice (cascades to items if mapped correctly)
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 4. Generate PDF and save path
        String pdfPath = pdfGeneratorService.generateInvoicePdf(savedInvoice);
        savedInvoice.setPdfPath(pdfPath);

        return invoiceRepository.save(savedInvoice); // Save PDF path
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoicesByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }

        return invoiceRepository.findByCustomerId(customerId);
    }

    public Invoice getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId));
    }

    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new InvoiceNotFoundException("Invoice not found with ID: " + id);
        }
        invoiceRepository.deleteById(id);
    }

    // Update PDF path (used after regeneration)
    public void updatePdfPath(Long invoiceId, String pdfPath) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId));
        invoice.setPdfPath(pdfPath);
        invoiceRepository.save(invoice);
    }
}
