package com.invoice.Invoice_System.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name="invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class InvoiceItem{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name cannot be blank")
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be a positive number")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be positive or zero")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Discount cannot be null")
    @PositiveOrZero(message = "Discount must be positive or zero")
    @Column(name = "discount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;

    @Transient
    private BigDecimal finalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Invoice invoice;

    public BigDecimal getFinalPrice() {
        if (quantity == null || price == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));
        if (discount != null) {
            itemTotal = itemTotal.subtract(discount);
        }
        return itemTotal.max(BigDecimal.ZERO);
    }
}