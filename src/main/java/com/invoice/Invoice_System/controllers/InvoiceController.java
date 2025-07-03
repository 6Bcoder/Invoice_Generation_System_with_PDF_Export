package com.invoice.Invoice_System.controllers;

import com.invoice.Invoice_System.model.Customer;
import org.springframework.core.io.Resource;
import com.invoice.Invoice_System.model.Invoice;
import com.invoice.Invoice_System.service.CustomerService;
import com.invoice.Invoice_System.service.EmailService;
import com.invoice.Invoice_System.service.InvoiceService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerService customerService;

    // Show invoice creation form (GET)
    @GetMapping("/new")
    public String showInvoiceForm(Model model) {
        model.addAttribute("invoice", new Invoice());
        model.addAttribute("customers", customerService.getAllCustomers());
        return "add_invoice"; // This Thymeleaf page must exist
    }

    // Create invoice (POST)
    @PostMapping("/save")
    public String createInvoice(@RequestParam("customerId") Long customerId,
                                @ModelAttribute Invoice invoice,
                                RedirectAttributes redirectAttributes) {
        Invoice savedInvoice = invoiceService.createInvoice(customerId, invoice);
        redirectAttributes.addFlashAttribute("message", "Invoice created successfully with ID: " + savedInvoice.getId());
        return "redirect:/invoices/list";
    }

    // View all invoices
    @GetMapping("/list")
    public String listInvoices(Model model) {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        model.addAttribute("invoices", invoices);
        return "list_invoices";
    }

    // View single invoice (PDF link or info)
    @GetMapping("/view/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        model.addAttribute("invoice", invoice);
        return "view_invoice";
    }

    // Download PDF
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadInvoicePdf(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        File pdfFile = new File(invoice.getPdfPath());

        if (!pdfFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(pdfFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename("invoice_" + id + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfFile.length())
                .body(resource);
    }


    // Email PDF
    @PostMapping("/email/{id}")
    public String emailInvoice(@PathVariable Long id,
                               @RequestParam("to") String recipient,
                               RedirectAttributes redirectAttributes) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(id);
            emailService.sendInvoiceEmail(
                    recipient,
                    "Your Shopping Mall Invoice",
                    "Thank you for your purchase. Please find your invoice attached.",
                    invoice.getPdfPath()
            );
            redirectAttributes.addFlashAttribute("message", "Invoice emailed successfully to " + recipient);
        } catch (MessagingException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send email: " + e.getMessage());
        }
        return "redirect:/invoices/list";
    }

    // Delete invoice
    @GetMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        invoiceService.deleteInvoice(id);
        redirectAttributes.addFlashAttribute("message", "Invoice deleted successfully.");
        return "redirect:/invoices/list";
    }

    // Regenerate PDF
    @GetMapping("/regenerate/{id}")
    public String regeneratePdf(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        String newPdfPath = invoiceService
                .createInvoice(invoice.getCustomer().getId(), invoice)
                .getPdfPath();
        invoiceService.updatePdfPath(id, newPdfPath);
        redirectAttributes.addFlashAttribute("message", "PDF regenerated successfully.");
        return "redirect:/invoices/view/" + id;
    }

    @GetMapping("/regenerate-by-customer")
    public String showRegeneratePage(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "regenerate_invoice";
    }

    @GetMapping("/regenerate-by-customer/list")
    public String showInvoicesForCustomer(@RequestParam("customerId") Long customerId, Model model) {
        List<Invoice> invoices = invoiceService.getInvoicesByCustomerId(customerId);
        model.addAttribute("invoices", invoices);
        return "regenerated_invoice_list";
    }




}

