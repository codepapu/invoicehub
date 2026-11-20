package com.invoicehub.service;

import com.invoicehub.entity.Customer;
import com.invoicehub.entity.Invoice;
import com.invoicehub.entity.InvoiceDetail;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Builds the PDF of an invoice with OpenPDF.
 * <p>
 * Compare with the OpenXava edition, where the same report is a JasperReports design
 * plus a ten-line action. Jmix has a Reports add-on for this, but it is a paid one.
 */
@Component("ihub_InvoicePdfService")
public class InvoicePdfService {

    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26);
    private static final Font BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font MUTED = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, Color.GRAY);
    private static final Color HEADER_BACKGROUND = new Color(0xE8, 0xE8, 0xF4);

    public byte[] createPdf(Invoice invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            addHeader(document, invoice);
            addCustomer(document, invoice.getCustomer());
            addLines(document, invoice);
            addTotals(document, invoice);
            if (invoice.getRemarks() != null && !invoice.getRemarks().isBlank()) {
                Paragraph remarks = new Paragraph(invoice.getRemarks(), MUTED);
                remarks.setSpacingBefore(24);
                document.add(remarks);
            }
        } finally {
            document.close();
        }
        return out.toByteArray();
    }

    private void addHeader(Document document, Invoice invoice) {
        PdfPTable table = new PdfPTable(new float[]{3, 2});
        table.setWidthPercentage(100);

        PdfPCell left = noBorder(new Phrase());
        left.addElement(new Paragraph("INVOICE", TITLE));
        left.addElement(new Paragraph("InvoiceHub", MUTED));
        table.addCell(left);

        PdfPCell right = noBorder(new Phrase());
        right.addElement(rightAligned("Invoice no.  " + invoice.getNumber(), BOLD));
        right.addElement(rightAligned("Date  " + invoice.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)), NORMAL));
        table.addCell(right);

        document.add(table);
    }

    private void addCustomer(Document document, Customer customer) {
        Paragraph billTo = new Paragraph("Bill to", MUTED);
        billTo.setSpacingBefore(24);
        document.add(billTo);
        document.add(new Paragraph(customer.getName(), BOLD));
        if (customer.getCity() != null) {
            document.add(new Paragraph(customer.getCity(), NORMAL));
        }
        if (customer.getEmail() != null) {
            document.add(new Paragraph(customer.getEmail(), NORMAL));
        }
    }

    private void addLines(Document document, Invoice invoice) {
        PdfPTable table = new PdfPTable(new float[]{5, 1, 2, 2});
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);
        table.setHeaderRows(1);

        header(table, "Description", Element.ALIGN_LEFT);
        header(table, "Qty", Element.ALIGN_RIGHT);
        header(table, "Unit price", Element.ALIGN_RIGHT);
        header(table, "Amount", Element.ALIGN_RIGHT);

        if (invoice.getDetails() != null) {
            for (InvoiceDetail detail : invoice.getDetails()) {
                cell(table, detail.getProduct().getDescription(), Element.ALIGN_LEFT);
                cell(table, String.valueOf(detail.getQuantity()), Element.ALIGN_RIGHT);
                cell(table, money(detail.getUnitPrice()), Element.ALIGN_RIGHT);
                cell(table, money(detail.getAmount()), Element.ALIGN_RIGHT);
            }
        }
        document.add(table);
    }

    private void addTotals(Document document, Invoice invoice) {
        PdfPTable table = new PdfPTable(new float[]{3, 1.2f, 1});
        table.setWidthPercentage(100);
        table.setSpacingBefore(14);

        totalRow(table, "Amounts sum", money(invoice.getAmountsSum()), NORMAL);
        totalRow(table, "VAT " + invoice.getVatPercentage() + " %", money(invoice.getVat()), NORMAL);
        totalRow(table, "Total", money(invoice.getTotal()), BOLD);

        document.add(table);
    }

    private void totalRow(PdfPTable table, String label, String value, Font font) {
        table.addCell(noBorder(new Phrase("")));
        PdfPCell labelCell = noBorder(new Phrase(label, font));
        table.addCell(labelCell);
        PdfPCell valueCell = noBorder(new Phrase(value, font));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private void header(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BOLD));
        cell.setBackgroundColor(HEADER_BACKGROUND);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void cell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private PdfPCell noBorder(Phrase phrase) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        return cell;
    }

    private Paragraph rightAligned(String text, Font font) {
        Paragraph paragraph = new Paragraph(new Chunk(text, font));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        return paragraph;
    }

    private String money(BigDecimal value) {
        return MoneyFormatter.format(value);
    }
}
