package com.debaets.crud.model.dto;

import com.debaets.crud.model.Gender;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Data
public class UserDto {

    private Long id;
    @Length(min = 3)
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;

}
