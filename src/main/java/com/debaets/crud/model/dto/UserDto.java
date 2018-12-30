package com.debaets.crud.model.dto;

import com.debaets.crud.model.Gender;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;

}
