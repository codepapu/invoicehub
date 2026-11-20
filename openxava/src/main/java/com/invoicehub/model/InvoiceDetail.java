package com.invoicehub.model;

import java.math.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

import com.invoicehub.calculators.*;

import lombok.*;

/**
 * A line of an invoice. It is an @Embeddable used as @ElementCollection
 * in Invoice, so it has no screen of its own.
 */
@Embeddable @Getter @Setter
public class InvoiceDetail {

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@DescriptionsList(descriptionProperties="number, description")
	Product product;

	@Min(1)
	int quantity;

	// Proposed from the selected product, but the user can change it
	@Money @Required @DecimalMin("0")
	@DefaultValueCalculator(value=UnitPriceCalculator.class,
		properties=@PropertyValue(name="productId", from="product.id"))
	BigDecimal unitPrice;

	@Money @Depends("unitPrice, quantity")
	public BigDecimal getAmount() {
		if (unitPrice == null) return BigDecimal.ZERO;
		return unitPrice.multiply(new BigDecimal(quantity));
	}

}
