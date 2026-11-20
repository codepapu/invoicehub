package com.invoicehub.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.Composition;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "IHUB_INVOICE", indexes = {
        @Index(name = "IDX_IHUB_INVOICE_CUSTOMER", columnList = "CUSTOMER_ID")
}, uniqueConstraints = {
        @UniqueConstraint(name = "UC_IHUB_INVOICE_NUMBER", columnNames = {"INVOICE_NUMBER"})
})
@Entity(name = "ihub_Invoice")
public class Invoice {

    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    @Column(name = "INVOICE_NUMBER", nullable = false)
    @NotNull
    private Integer number;

    @Column(name = "INVOICE_DATE", nullable = false)
    @NotNull
    private LocalDate date;

    @Column(name = "VAT_PERCENTAGE", nullable = false)
    @NotNull
    @Min(0)
    @Max(100)
    private Integer vatPercentage = 20;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    private Customer customer;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "invoice")
    @NotEmpty(message = "{msg://com.invoicehub.entity/Invoice.details.notEmpty}")
    private List<InvoiceDetail> details;

    @Column(name = "REMARKS", length = 500)
    private String remarks;

    @InstanceName
    @DependsOnProperties({"number"})
    public String getInstanceName() {
        return "Invoice " + number;
    }

    @JmixProperty
    @DependsOnProperties({"details"})
    public BigDecimal getAmountsSum() {
        BigDecimal sum = BigDecimal.ZERO;
        if (details != null) {
            for (InvoiceDetail detail : details) {
                sum = sum.add(detail.getAmount());
            }
        }
        return sum;
    }

    @JmixProperty
    @DependsOnProperties({"details", "vatPercentage"})
    public BigDecimal getVat() {
        int percentage = vatPercentage == null ? 0 : vatPercentage;
        return getAmountsSum().multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @JmixProperty
    @DependsOnProperties({"details", "vatPercentage"})
    public BigDecimal getTotal() {
        return getAmountsSum().add(getVat());
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getVatPercentage() {
        return vatPercentage;
    }

    public void setVatPercentage(Integer vatPercentage) {
        this.vatPercentage = vatPercentage;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<InvoiceDetail> getDetails() {
        return details;
    }

    public void setDetails(List<InvoiceDetail> details) {
        this.details = details;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
