package com.invoicehub.model;

import java.util.*;

import jakarta.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
@Tab(properties="number, name, email, city")
public class Customer extends Identifiable {

	@Column(name="CUSTOMER_NUMBER") @Required
	int number;

	@Column(length=50) @Required
	String name;

	@Stereotype("EMAIL") @Column(length=60)
	String email;

	@Column(length=50)
	String city;

	// Customer-to-invoices view: all the invoices of this customer, read only
	@OneToMany(mappedBy="customer")
	@ReadOnly
	@ListProperties("number, date, vatPercentage, amountsSum, total")
	Collection<Invoice> invoices;

}
