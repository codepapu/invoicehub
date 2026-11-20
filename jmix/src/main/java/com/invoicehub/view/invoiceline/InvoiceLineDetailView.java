package com.invoicehub.view.invoiceline;

import com.invoicehub.entity.InvoiceDetail;
import com.invoicehub.entity.Product;
import com.invoicehub.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

/**
 * Dialog to edit one line of an invoice. The unit price is proposed from the selected product.
 */
@Route(value = "invoice-lines/:id", layout = MainView.class)
@ViewController(id = "ihub_InvoiceDetail.detail")
@ViewDescriptor(path = "invoice-line-detail-view.xml")
@EditedEntityContainer("invoiceDetailDc")
public class InvoiceLineDetailView extends StandardDetailView<InvoiceDetail> {

    @Subscribe(id = "invoiceDetailDc", target = Target.DATA_CONTAINER)
    public void onInvoiceDetailDcItemPropertyChange(
            final io.jmix.flowui.model.InstanceContainer.ItemPropertyChangeEvent<InvoiceDetail> event) {
        if ("product".equals(event.getProperty())) {
            Product product = (Product) event.getValue();
            getEditedEntity().setUnitPrice(product != null ? product.getUnitPrice() : null);
        }
    }
}
