package com.invoicehub.calculators;

import java.math.*;

import org.openxava.calculators.*;
import org.openxava.jpa.*;

import com.invoicehub.model.*;

import lombok.*;

/** Proposes the unit price of a invoice line from the selected product. */
public class UnitPriceCalculator implements ICalculator {

	@Getter @Setter
	String productId;

	public Object calculate() throws Exception {
		if (productId == null || productId.isEmpty()) return BigDecimal.ZERO;
		Product product = XPersistence.getManager().find(Product.class, productId);
		return product == null ? BigDecimal.ZERO : product.getUnitPrice();
	}

}
