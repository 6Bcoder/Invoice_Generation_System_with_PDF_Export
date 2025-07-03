package com.invoice.Invoice_System.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"invoices"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "phone_number")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @OneToMany(mappedBy = "customer" , cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private Set<Invoice> invoices = new HashSet<>();


    public void addInvoice(Invoice invoice) {
        this.invoices.add(invoice);
        invoice.setCustomer(this);
    }

    public void removeInvoice(Invoice invoice) {
        this.invoices.remove(invoice);
        invoice.setCustomer(null);
    }



}
