package com.invoicehub.invoice;

import com.invoicehub.entity.Customer;
import com.invoicehub.entity.Invoice;
import com.invoicehub.entity.InvoiceDetail;
import com.invoicehub.entity.Product;
import com.invoicehub.test_support.AuthenticatedAsAdmin;
import io.jmix.core.DataManager;
import io.jmix.core.SaveContext;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Saves real invoices through {@link DataManager}: composition, constraints and validation. */
@SpringBootTest
@ExtendWith(AuthenticatedAsAdmin.class)
@ActiveProfiles("test")
class InvoiceIntegrationTest {

    private static final int NUMBER = 987_001;

    @Autowired
    DataManager dataManager;

    private Customer customer;
    private Product product;

    @AfterEach
    void tearDown() {
        // Remove in dependency order; ignore what a failing test never created.
        dataManager.load(InvoiceDetail.class).query("e.invoice.number >= :n").parameter("n", NUMBER).list()
                .forEach(dataManager::remove);
        dataManager.load(Invoice.class).query("e.number >= :n").parameter("n", NUMBER).list()
                .forEach(dataManager::remove);
        if (customer != null && customer.getId() != null) dataManager.remove(customer);
        if (product != null && product.getId() != null) dataManager.remove(product);
    }

    @Test
    void invoiceWithLines_isSavedWithItsLinesAndTotals() {
        Invoice saved = saveInvoice(NUMBER, 1);

        Invoice loaded = dataManager.load(Invoice.class).id(saved.getId())
                .fetchPlan(fp -> fp.addFetchPlan("_base").add("details", d -> d.addFetchPlan("_base")))
                .one();

        assertThat(loaded.getDetails()).hasSize(1);
        assertThat(loaded.getAmountsSum()).isEqualByComparingTo("200.00");
        assertThat(loaded.getVat()).isEqualByComparingTo("40.00");
        assertThat(loaded.getTotal()).isEqualByComparingTo("240.00");
    }

    @Test
    void duplicateInvoiceNumber_isRejected() {
        saveInvoice(NUMBER, 1);

        assertThatThrownBy(() -> saveInvoice(NUMBER, 1)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void invoiceWithoutLines_isRejected() {
        createCustomerAndProduct();
        Invoice invoice = newInvoice(NUMBER + 1);

        assertThatThrownBy(() -> dataManager.save(invoice))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void customerWithInvoices_cannotBeDeleted() {
        Invoice saved = saveInvoice(NUMBER, 1);

        assertThatThrownBy(() -> dataManager.remove(saved.getCustomer()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void deletingTheInvoice_deletesItsLines() {
        Invoice saved = saveInvoice(NUMBER, 2);
        dataManager.remove(saved);

        assertThat(dataManager.load(InvoiceDetail.class).query("e.invoice.id = :id")
                .parameter("id", saved.getId()).list()).isEmpty();
    }

    private Invoice saveInvoice(int number, int lines) {
        createCustomerAndProduct();
        Invoice invoice = newInvoice(number);
        List<InvoiceDetail> details = new ArrayList<>();
        for (int i = 0; i < lines; i++) {
            InvoiceDetail detail = dataManager.create(InvoiceDetail.class);
            detail.setInvoice(invoice);
            detail.setProduct(product);
            detail.setQuantity(2);
            detail.setUnitPrice(new BigDecimal("100.00"));
            details.add(detail);
        }
        invoice.setDetails(details);
        SaveContext context = new SaveContext().saving(invoice);
        details.forEach(context::saving);
        return dataManager.save(context).get(invoice);
    }

    private Invoice newInvoice(int number) {
        Invoice invoice = dataManager.create(Invoice.class);
        invoice.setNumber(number);
        invoice.setDate(LocalDate.of(2026, 1, 1));
        invoice.setCustomer(customer);
        return invoice;
    }

    private void createCustomerAndProduct() {
        if (customer != null) return;
        customer = dataManager.create(Customer.class);
        customer.setNumber(987_001);
        customer.setName("Test customer");
        customer = dataManager.save(customer);
        product = dataManager.create(Product.class);
        product.setNumber(987_001);
        product.setDescription("Test product");
        product.setUnitPrice(new BigDecimal("100.00"));
        product = dataManager.save(product);
    }
}
