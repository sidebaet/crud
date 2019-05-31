package com.debaets.crud.core.service.model;

import com.debaets.crud.core.model.CrudEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
@ToString
public class User implements CrudEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String firstName;
	private String lastName;
	private String email;

	private int age;

	@Embedded
	private Address address;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private Date birthday;

	private LocalDate weddingDate;

	@OneToMany(mappedBy = "user")
	private List<Period> periods;

	private Boolean isAlive;
}
