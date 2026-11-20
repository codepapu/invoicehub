package com.invoicehub.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A line of an invoice. Owned by the invoice (composition).
 */
@JmixEntity
@Table(name = "IHUB_INVOICE_DETAIL", indexes = {
        @Index(name = "IDX_IHUB_INVOICE_DETAIL_INVOICE", columnList = "INVOICE_ID"),
        @Index(name = "IDX_IHUB_INVOICE_DETAIL_PRODUCT", columnList = "PRODUCT_ID")
})
@Entity(name = "ihub_InvoiceDetail")
public class InvoiceDetail {

    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    @JoinColumn(name = "INVOICE_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    private Invoice invoice;

    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    private Product product;

    @Column(name = "QUANTITY", nullable = false)
    @NotNull
    @Min(1)
    private Integer quantity = 1;

    @Column(name = "UNIT_PRICE", nullable = false, precision = 19, scale = 2)
    @NotNull
    @DecimalMin("0")
    private BigDecimal unitPrice;

    @InstanceName
    @DependsOnProperties({"product", "quantity"})
    public String getInstanceName() {
        return (product != null ? product.getInstanceName() : "") + " x " + quantity;
    }

    @JmixProperty
    @DependsOnProperties({"unitPrice", "quantity"})
    public BigDecimal getAmount() {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
