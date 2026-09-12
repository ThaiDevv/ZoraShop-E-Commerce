package com.example.zorashopminishopee.module.users.dto.request;

import com.example.zorashopminishopee.module.oder.enums.Sex;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
     private String email;
     private String password;

     @JsonProperty("fullname")
     @JsonAlias({"fullName", "name"})
     private String fullname;

     private String phone;

     @JsonProperty("birthDay")
     @JsonAlias({"BirthDay", "birthDate", "dateOfBirth"})
     private LocalDate BirthDay;

     private Sex sex;
}
