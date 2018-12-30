package com.debaets.crud.core.model;

import java.io.Serializable;

public interface CrudEntity<ID> extends Serializable {

	ID getId();

	void setId(ID id);

}
