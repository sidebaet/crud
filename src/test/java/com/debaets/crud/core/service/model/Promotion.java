package com.debaets.crud.core.service.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {

	@EmbeddedId
	private Id id;

	@Embeddable
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Id implements Serializable {
		private String promotionId;
		private String container;
	}

}
