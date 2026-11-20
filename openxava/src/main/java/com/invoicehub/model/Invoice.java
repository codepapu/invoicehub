package com.invoicehub.model;

import java.math.*;
import java.time.*;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.*;

import com.invoicehub.calculators.*;

import lombok.*;

@Entity @Getter @Setter
@View(members=
	"number, date, vatPercentage;" +
	"customer;" +
	"details;" +
	"remarks;" +
	"amountsSum, vat, total"
)
@Tab(properties="number, date, customer.name, total",
	defaultOrder="${number} desc")
public class Invoice extends Identifiable {

	// Unique: two invoices must never share a number
	@Column(name="INVOICE_NUMBER", unique=true) @ReadOnly
	@DefaultValueCalculator(NextInvoiceNumberCalculator.class)
	int number;

	@Column(name="INVOICE_DATE") @Required
	@DefaultValueCalculator(CurrentLocalDateCalculator.class)
	LocalDate date;

	@Required @Min(0) @Max(100)
	@DefaultValueCalculator(value=IntegerCalculator.class,
		properties=@PropertyValue(name="value", value="20"))
	int vatPercentage;

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@DescriptionsList(descriptionProperties="number, name")
	Customer customer;

	@ElementCollection
	@Size(min=1)
	@ListProperties("product.number, product.description, quantity, unitPrice, amount[invoice.amountsSum]")
	List<InvoiceDetail> details;

	@Stereotype("MEMO")
	String remarks;

	@Money @Depends("details.amount")
	public BigDecimal getAmountsSum() {
		BigDecimal sum = BigDecimal.ZERO;
		if (details != null) for (InvoiceDetail detail: details) sum = sum.add(detail.getAmount());
		return sum;
	}

	@Money @Depends("amountsSum, vatPercentage")
	public BigDecimal getVat() {
		return getAmountsSum().multiply(new BigDecimal(vatPercentage)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
	}

	@Money @Depends("amountsSum, vat")
	public BigDecimal getTotal() {
		return getAmountsSum().add(getVat());
	}

}
