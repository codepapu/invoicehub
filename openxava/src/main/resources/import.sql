-- Demo data, loaded on every start (the in-memory database is recreated each time)
insert into Category (id, name) values ('c0000000000000000000000000000001', 'Software');
insert into Category (id, name) values ('c0000000000000000000000000000002', 'Hardware');
insert into Category (id, name) values ('c0000000000000000000000000000003', 'Services');
insert into Customer (id, CUSTOMER_NUMBER, name, email, city) values ('a0000000000000000000000000000001', 1, 'Acme Corporation', 'billing@acme.example', 'New York');
insert into Customer (id, CUSTOMER_NUMBER, name, email, city) values ('a0000000000000000000000000000002', 2, 'Globex Ltd', 'accounts@globex.example', 'London');
insert into Customer (id, CUSTOMER_NUMBER, name, email, city) values ('a0000000000000000000000000000003', 3, 'Initech', 'finance@initech.example', 'Hyderabad');
insert into Product (id, PRODUCT_NUMBER, description, category_id, unitPrice) values ('b0000000000000000000000000000001', 1, 'Annual software license', 'c0000000000000000000000000000001', 1200.00);
insert into Product (id, PRODUCT_NUMBER, description, category_id, unitPrice) values ('b0000000000000000000000000000002', 2, 'Laptop 14 inch', 'c0000000000000000000000000000002', 950.00);
insert into Product (id, PRODUCT_NUMBER, description, category_id, unitPrice) values ('b0000000000000000000000000000003', 3, 'Wireless mouse', 'c0000000000000000000000000000002', 25.50);
insert into Product (id, PRODUCT_NUMBER, description, category_id, unitPrice) values ('b0000000000000000000000000000004', 4, 'Consulting hour', 'c0000000000000000000000000000003', 90.00);
insert into Invoice (id, INVOICE_NUMBER, INVOICE_DATE, vatPercentage, customer_id, remarks) values ('d0000000000000000000000000000001', 1, '2026-10-05', 20, 'a0000000000000000000000000000001', 'Thank you for your business');
insert into Invoice_details (Invoice_id, product_id, quantity, unitPrice) values ('d0000000000000000000000000000001', 'b0000000000000000000000000000001', 2, 1200.00);
insert into Invoice_details (Invoice_id, product_id, quantity, unitPrice) values ('d0000000000000000000000000000001', 'b0000000000000000000000000000004', 10, 90.00);
insert into Invoice (id, INVOICE_NUMBER, INVOICE_DATE, vatPercentage, customer_id, remarks) values ('d0000000000000000000000000000002', 2, '2026-11-02', 20, 'a0000000000000000000000000000002', null);
insert into Invoice_details (Invoice_id, product_id, quantity, unitPrice) values ('d0000000000000000000000000000002', 'b0000000000000000000000000000002', 3, 950.00);
insert into Invoice_details (Invoice_id, product_id, quantity, unitPrice) values ('d0000000000000000000000000000002', 'b0000000000000000000000000000003', 3, 25.50);
