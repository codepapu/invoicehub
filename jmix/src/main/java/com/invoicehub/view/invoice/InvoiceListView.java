package com.invoicehub.view.invoice;

import com.invoicehub.entity.Invoice;
import com.invoicehub.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "invoices", layout = MainView.class)
@ViewController(id = "ihub_Invoice.list")
@ViewDescriptor(path = "invoice-list-view.xml")
@LookupComponent("invoicesDataGrid")
@DialogMode(width = "64em")
public class InvoiceListView extends StandardListView<Invoice> {
}
