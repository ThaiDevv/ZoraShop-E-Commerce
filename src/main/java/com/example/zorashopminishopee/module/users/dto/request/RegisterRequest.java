package com.example.zorashopminishopee.module.users.dto.request;

import com.example.zorashopminishopee.module.users.enums.Sex;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
     @NotBlank(message = "Email is required")
     @Email(message = "Email is invalid")
     @Size(max = 255)
     private String email;

     @NotBlank(message = "Password is required")
     @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
     private String password;

     @JsonProperty("fullname")
     @JsonAlias({"fullName", "name"})
     @NotBlank(message = "Full name is required")
     @Size(max = 255)
     private String fullname;

     @NotBlank(message = "Phone is required")
     @Pattern(regexp = ValidationPatterns.PHONE, message = "Phone number is invalid")
     private String phone;

     @JsonProperty("birthDay")
     @JsonAlias({"BirthDay", "birthDate", "dateOfBirth"})
     @Past(message = "Birth day must be in the past")
     private LocalDate BirthDay;

     private Sex sex;
}
