package com.invoicehub.model;

import jakarta.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
public class Category extends Identifiable {

	@Column(length=40) @Required
	String name;

}
