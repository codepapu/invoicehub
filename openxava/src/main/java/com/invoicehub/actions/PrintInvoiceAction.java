package com.invoicehub.actions;

import java.math.*;
import java.text.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.invoicehub.model.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

/**
 * Prints the invoice shown in the detail view as a PDF, using the
 * JasperReports design in src/main/resources/reports/Invoice.jrxml.
 */
public class PrintInvoiceAction extends JasperReportBaseAction {

	private Invoice invoice;

	public String getFormat() throws Exception {
		return PDF;
	}

	protected String getJRXML() throws Exception {
		return "Invoice.jrxml";
	}

	protected Map getParameters() throws Exception {
		Invoice invoice = getInvoice();
		Customer customer = invoice.getCustomer();
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("number", invoice.getNumber());
		parameters.put("date", DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH)
			.format(java.sql.Date.valueOf(invoice.getDate())));
		parameters.put("customerName", customer.getName());
		parameters.put("customerEmail", customer.getEmail());
		parameters.put("customerCity", customer.getCity());
		parameters.put("vatPercentage", invoice.getVatPercentage());
		parameters.put("amountsSum", invoice.getAmountsSum());
		parameters.put("vat", invoice.getVat());
		parameters.put("total", invoice.getTotal());
		parameters.put("remarks", invoice.getRemarks());
		return parameters;
	}

	protected JRDataSource getDataSource() throws Exception {
		List<Map<String, ?>> lines = new ArrayList<>();
		for (InvoiceDetail detail: getInvoice().getDetails()) {
			Map<String, Object> line = new HashMap<>();
			line.put("description", detail.getProduct().getDescription());
			line.put("quantity", detail.getQuantity());
			line.put("unitPrice", detail.getUnitPrice());
			line.put("amount", detail.getAmount());
			lines.add(line);
		}
		return new JRMapCollectionDataSource((Collection) lines);
	}

	private Invoice getInvoice() throws Exception {
		if (invoice == null) {
			String id = (String) getView().getValue("id");
			if (id == null) throw new org.openxava.util.XavaException("Save the invoice before printing it");
			invoice = XPersistence.getManager().find(Invoice.class, id);
		}
		return invoice;
	}

}
