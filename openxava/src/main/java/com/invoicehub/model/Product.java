package com.invoicehub.model;

import java.math.*;

import jakarta.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
@Tab(properties="number, description, category.name, unitPrice")
public class Product extends Identifiable {

	@Column(name="PRODUCT_NUMBER") @Required
	int number;

	@Column(length=60) @Required
	String description;

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="name")
	Category category;

	@Money @Required
	BigDecimal unitPrice;

}
