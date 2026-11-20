package com.invoicehub.invoice;

import com.invoicehub.entity.Customer;
import com.invoicehub.entity.Invoice;
import com.invoicehub.entity.InvoiceDetail;
import com.invoicehub.entity.Product;
import com.invoicehub.service.InvoicePdfService;
import com.invoicehub.service.MoneyFormatter;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Plain unit tests: no Spring context, no database, so they run in a second. */
class InvoiceTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void totals_areCalculatedFromTheLines() {
        Invoice invoice = invoice(20, line("2", "1200.00"), line("10", "90.00"));

        assertThat(invoice.getAmountsSum()).isEqualByComparingTo("3300.00");
        assertThat(invoice.getVat()).isEqualByComparingTo("660.00");
        assertThat(invoice.getTotal()).isEqualByComparingTo("3960.00");
    }

    @Test
    void vat_isRoundedHalfUpToTwoDecimals() {
        // 0.05 * 7 % = 0.0035 -> 0.00 ; 0.75 * 7 % = 0.0525 -> 0.05 ; 1.50 * 7 % = 0.105 -> 0.11
        assertThat(invoice(7, line("1", "1.50")).getVat()).isEqualByComparingTo("0.11");
        assertThat(invoice(7, line("1", "0.75")).getVat()).isEqualByComparingTo("0.05");
    }

    @Test
    void invoiceWithoutLines_hasZeroTotals() {
        Invoice invoice = invoice(20);
        assertThat(invoice.getAmountsSum()).isEqualByComparingTo("0");
        assertThat(invoice.getTotal()).isEqualByComparingTo("0");
    }

    @Test
    void validation_rejectsBadVatQuantityAndEmptyInvoice() {
        Invoice invoice = invoice(120);
        assertThat(validator.validateProperty(invoice, "vatPercentage")).isNotEmpty();
        assertThat(validator.validateProperty(invoice, "details")).isNotEmpty();

        invoice.setVatPercentage(-1);
        assertThat(validator.validateProperty(invoice, "vatPercentage")).isNotEmpty();

        InvoiceDetail detail = line("0", "10.00");
        assertThat(validator.validateProperty(detail, "quantity")).isNotEmpty();
        detail.setQuantity(1);
        detail.setUnitPrice(new BigDecimal("-1"));
        assertThat(validator.validateProperty(detail, "unitPrice")).isNotEmpty();
    }

    @Test
    void validation_acceptsValidValues() {
        Invoice invoice = invoice(20, line("1", "10.00"));
        assertThat(validator.validateProperty(invoice, "vatPercentage")).isEmpty();
        assertThat(validator.validateProperty(invoice, "details")).isEmpty();
    }

    @Test
    void moneyFormatter_usesThousandsSeparatorAndTwoDecimals() {
        assertThat(MoneyFormatter.format(new BigDecimal("3960"))).isEqualTo("3,960.00");
        assertThat(MoneyFormatter.format(null)).isEqualTo("0.00");
    }

    @Test
    void pdf_isGenerated() {
        Invoice invoice = invoice(20, line("2", "1200.00"));
        Customer customer = new Customer();
        customer.setName("Acme Corporation");
        invoice.setCustomer(customer);
        invoice.setNumber(1);
        invoice.setDate(LocalDate.of(2026, 10, 5));

        byte[] pdf = new InvoicePdfService().createPdf(invoice);

        assertThat(new String(pdf, 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
        assertThat(pdf.length).isGreaterThan(1000);
    }

    private Invoice invoice(int vat, InvoiceDetail... lines) {
        Invoice invoice = new Invoice();
        invoice.setVatPercentage(vat);
        invoice.setDetails(lines.length == 0 ? null : List.of(lines));
        return invoice;
    }

    private InvoiceDetail line(String quantity, String unitPrice) {
        Product product = new Product();
        product.setDescription("Item");
        InvoiceDetail detail = new InvoiceDetail();
        detail.setProduct(product);
        detail.setQuantity(Integer.valueOf(quantity));
        detail.setUnitPrice(new BigDecimal(unitPrice));
        return detail;
    }
}
