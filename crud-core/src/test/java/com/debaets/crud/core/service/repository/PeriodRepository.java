package com.debaets.crud.core.service.repository;

import com.debaets.crud.core.repository.CrudRepository;
import com.debaets.crud.core.service.model.Period;
import org.springframework.stereotype.Repository;

@Repository("testPeriodRepository")
public interface PeriodRepository extends CrudRepository<Period, Long> {
}
