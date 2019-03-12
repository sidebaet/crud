package com.debaets.crud.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.model.Gender;

import lombok.Data;

@Data
@Entity
public class User implements CrudEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;

}
