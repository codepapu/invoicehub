# InvoiceHub (Jmix edition)

The same invoicing application as `../openxava`, built with [Jmix](https://www.jmix.io) 3.1
(Spring Boot, Vaadin Flow UI, EclipseLink, Liquibase) and an embedded file-based HSQLDB.

Unlike OpenXava, Jmix does not generate screens at runtime: every list and form is an XML view
descriptor plus a Java controller. You get more control over the UI and more code to write.

## Requirements

- Java 21
- No database install (HSQLDB file under `.jmix/hsqldb`)
- The first run builds the Vaadin frontend bundle (`npm install`), which takes a few minutes

## Run

```bash
./gradlew bootRun
```

Open <http://localhost:8080> and sign in with `admin` / `admin`.

If the Gradle wrapper download is slow, any installed Gradle 9.x works: `gradle bootRun`.

## What is in it

| Piece | Where |
|---|---|
| Entities (`Category`, `Customer`, `Product`, `Invoice`, `InvoiceDetail`) | `src/main/java/com/invoicehub/entity` |
| Schema and demo data | `src/main/resources/com/invoicehub/liquibase/changelog/020-invoicing-model.xml` |
| Views (list + detail per entity) | `src/main/java/com/invoicehub/view/*` and `src/main/resources/com/invoicehub/view/*` |
| Customer-to-invoices view | read-only invoices grid in `view/customer/customer-detail-view.xml` |
| Invoice PDF | `service/InvoicePdfService.java` (OpenPDF), called by the **Print invoice (PDF)** button |
| Live totals in the invoice form | `view/invoice/InvoiceDetailView.java` (`updateTotals()`) |
| Unit price proposed from product | `view/invoiceline/InvoiceLineDetailView.java` |

Why OpenPDF instead of a report designer: the Jmix Reports add-on is commercial.

## Tests

```bash
./gradlew test --tests 'com.invoicehub.invoice.*'
```

Unit tests (totals, VAT rounding, validation, money format, PDF) plus integration tests that save real invoices (lines, unique number, customer protected from deletion, lines deleted with their invoice). Run everything with `./gradlew test`; integration tests use a separate database file (`.jmix/hsqldb/ihub_test`).

## Notes

- Invoice numbers are unique (database constraint, changelog `030`). VAT must be 0-100,
  quantity at least 1, unit price not negative, and an invoice needs at least one line.

- Change the admin password and remove `ui.login.defaultUsername` / `ui.login.defaultPassword`
  from `application.properties` before deploying.
