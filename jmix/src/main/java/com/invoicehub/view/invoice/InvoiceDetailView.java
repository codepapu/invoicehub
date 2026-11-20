package com.invoicehub.view.invoice;

import com.invoicehub.entity.Invoice;
import com.invoicehub.entity.InvoiceDetail;
import com.invoicehub.service.InvoicePdfService;
import com.invoicehub.service.MoneyFormatter;
import com.invoicehub.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.download.DownloadFormat;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Route(value = "invoices/:id", layout = MainView.class)
@ViewController(id = "ihub_Invoice.detail")
@ViewDescriptor(path = "invoice-detail-view.xml")
@EditedEntityContainer("invoiceDc")
public class InvoiceDetailView extends StandardDetailView<Invoice> {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private InvoicePdfService invoicePdfService;
    @Autowired
    private Downloader downloader;
    @Autowired
    private Notifications notifications;

    @ViewComponent
    private MessageBundle messageBundle;
    @ViewComponent
    private TypedTextField<String> amountsSumField;
    @ViewComponent
    private TypedTextField<String> vatField;
    @ViewComponent
    private TypedTextField<String> totalField;

    /** Defaults for a new invoice: next number and today's date (VAT 20 % is the entity default). */
    @Subscribe
    public void onInitEntity(final InitEntityEvent<Invoice> event) {
        Invoice invoice = event.getEntity();
        int last = dataManager.load(Invoice.class)
                .query("select e from ihub_Invoice e order by e.number desc")
                .maxResults(1)
                .optional()
                .map(Invoice::getNumber)
                .orElse(0);
        invoice.setNumber(last + 1);
        invoice.setDate(LocalDate.now());
    }

    @Subscribe
    public void onReady(final ReadyEvent event) {
        updateTotals();
    }

    @Subscribe(id = "detailsDc", target = Target.DATA_CONTAINER)
    public void onDetailsDcCollectionChange(final CollectionContainer.CollectionChangeEvent<InvoiceDetail> event) {
        updateTotals();
    }

    @Subscribe(id = "detailsDc", target = Target.DATA_CONTAINER)
    public void onDetailsDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<InvoiceDetail> event) {
        updateTotals();
    }

    @Subscribe(id = "invoiceDc", target = Target.DATA_CONTAINER)
    public void onInvoiceDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<Invoice> event) {
        updateTotals();
    }

    @Subscribe("printPdfButton")
    public void onPrintPdfButtonClick(final ClickEvent<JmixButton> event) {
        Invoice invoice = getEditedEntity();
        if (invoice.getCustomer() == null) {
            notifications.create(messageBundle.getMessage("selectCustomerFirst")).show();
            return;
        }
        byte[] pdf = invoicePdfService.createPdf(invoice);
        downloader.download(pdf, "invoice-" + invoice.getNumber() + ".pdf", DownloadFormat.PDF);
    }

    private void updateTotals() {
        Invoice invoice = getEditedEntity();
        amountsSumField.setTypedValue(money(invoice.getAmountsSum()));
        vatField.setTypedValue(money(invoice.getVat()));
        totalField.setTypedValue(money(invoice.getTotal()));
    }

    private String money(java.math.BigDecimal value) {
        return MoneyFormatter.format(value);
    }
}
