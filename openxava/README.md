# InvoiceHub (OpenXava edition)

A small invoicing application built with [OpenXava](https://www.openxava.org) 8 (plain/free edition),
Spring Boot and an embedded in-memory HSQLDB. No database installation is needed.

You write only JPA entities and a few annotations. OpenXava generates the screens
(lists, forms, search, filters, Excel/PDF export, charts, calendar views, i18n).

## Requirements

- Java 17+ (tested with Java 21)
- Maven 3.9+

## Run

```bash
mvn spring-boot:run
```

Open <http://localhost:8080/invoicehub> and sign in with `admin` / `admin`
(users live in `src/main/resources/naviox-users.properties`).

The database is recreated and reloaded from `src/main/resources/import.sql` on every start.

## What is in it

| Entity | Purpose |
|---|---|
| `Category` | Product categories |
| `Customer` | Customers, with a read-only list of all their invoices (customer-to-invoices view) |
| `Product` | Products with unit price and category (dropdown) |
| `Invoice` | Header, customer (dropdown), lines, VAT, calculated totals |
| `InvoiceDetail` | Invoice line (`@ElementCollection`); unit price is proposed from the product |

Things worth looking at in the code (`src/main/java/com/invoicehub`):

- `model/Invoice.java`: `@View` layout, `@Tab` list, `@DescriptionsList`, `@ElementCollection`,
  and calculated properties (`amountsSum`, `vat`, `total`) with `@Depends`.
- `model/Customer.java`: `@OneToMany(mappedBy="customer") @ReadOnly` collection showing each customer's invoices.
- `actions/PrintInvoiceAction.java` + `src/main/resources/reports/Invoice.jrxml`: the **Print invoice (PDF)**
  button on the invoice form, a JasperReports report (declared in `xava/controllers.xml`).
- `calculators/NextInvoiceNumberCalculator.java`: default value for the invoice number.
- `calculators/UnitPriceCalculator.java`: recalculated whenever the product of a line changes.

## Tests

```bash
mvn test
```

Fast unit tests (no database): totals, VAT rounding and validation rules.
Invoice numbers are unique, VAT must be 0-100, quantity at least 1, unit price not negative,
and an invoice needs at least one line.

## Package as a standalone jar

```bash
mvn clean package
java -jar target/invoicehub.war
```

## Switching to a real database

Edit `spring.datasource.*` in `application.properties`, add the JDBC driver to `pom.xml`,
and in `persistence.xml` change `schema-generation.database.action` from `create` to `update`
(and remove the `sql-load-script-source` line).
