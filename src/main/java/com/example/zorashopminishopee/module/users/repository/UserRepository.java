package com.example.zorashopminishopee.module.users.repository;

import com.example.zorashopminishopee.module.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);

}
