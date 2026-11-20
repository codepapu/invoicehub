package com.invoicehub.calculators;

import org.openxava.calculators.*;
import org.openxava.jpa.*;

/** Proposes the next invoice number: last one + 1. */
public class NextInvoiceNumberCalculator implements ICalculator {

	public Object calculate() throws Exception {
		Integer last = (Integer) XPersistence.getManager()
			.createQuery("select max(i.number) from Invoice i")
			.getSingleResult();
		return last == null ? 1 : last + 1;
	}

}
