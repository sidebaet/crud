package com.debaets.crud.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.debaets.crud.core.model.CrudEntity;

import lombok.Data;

@Data
@Entity
public class Contact implements CrudEntity<Long> {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String data;
}
