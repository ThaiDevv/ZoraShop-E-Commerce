package com.example.zorashopminishopee.module.users.dto.request;

import com.example.zorashopminishopee.module.oder.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
     private String email;
     private String password;
     private String fullname;
     private String phone;
     private LocalDate BirthDay;
     private Sex sex;
}
