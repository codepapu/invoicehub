package com.invoicehub.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.Test;

import jakarta.validation.*;

/** Plain unit tests: no OpenXava runtime, no database. */
class InvoiceTest {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void totalsAreCalculatedFromTheLines() {
		Invoice invoice = invoice(20, line(2, "1200.00"), line(10, "90.00"));
		assertEquals(0, new BigDecimal("3300.00").compareTo(invoice.getAmountsSum()));
		assertEquals(0, new BigDecimal("660.00").compareTo(invoice.getVat()));
		assertEquals(0, new BigDecimal("3960.00").compareTo(invoice.getTotal()));
	}

	@Test
	void vatIsRoundedHalfUpToTwoDecimals() {
		assertEquals(0, new BigDecimal("0.11").compareTo(invoice(7, line(1, "1.50")).getVat()));
		assertEquals(0, new BigDecimal("0.05").compareTo(invoice(7, line(1, "0.75")).getVat()));
	}

	@Test
	void invoiceWithoutLinesHasZeroTotals() {
		Invoice invoice = invoice(20);
		assertEquals(0, BigDecimal.ZERO.compareTo(invoice.getAmountsSum()));
		assertEquals(0, BigDecimal.ZERO.compareTo(invoice.getTotal()));
	}

	@Test
	void validationRejectsBadVatQuantityAndEmptyInvoice() {
		Invoice invoice = invoice(120);
		assertFalse(validator.validateProperty(invoice, "vatPercentage").isEmpty());
		assertFalse(validator.validateProperty(invoice, "details").isEmpty());
		invoice.setVatPercentage(-1);
		assertFalse(validator.validateProperty(invoice, "vatPercentage").isEmpty());

		InvoiceDetail detail = line(0, "10.00");
		assertFalse(validator.validateProperty(detail, "quantity").isEmpty());
		detail.setQuantity(1);
		detail.setUnitPrice(new BigDecimal("-1"));
		assertFalse(validator.validateProperty(detail, "unitPrice").isEmpty());
	}

	@Test
	void validationAcceptsValidValues() {
		Invoice invoice = invoice(20, line(1, "10.00"));
		assertTrue(validator.validateProperty(invoice, "vatPercentage").isEmpty());
		assertTrue(validator.validateProperty(invoice, "details").isEmpty());
	}

	private Invoice invoice(int vat, InvoiceDetail... lines) {
		Invoice invoice = new Invoice();
		invoice.setVatPercentage(vat);
		invoice.setDetails(new ArrayList<>(Arrays.asList(lines)));
		return invoice;
	}

	private InvoiceDetail line(int quantity, String unitPrice) {
		InvoiceDetail detail = new InvoiceDetail();
		detail.setQuantity(quantity);
		detail.setUnitPrice(new BigDecimal(unitPrice));
		return detail;
	}

}
