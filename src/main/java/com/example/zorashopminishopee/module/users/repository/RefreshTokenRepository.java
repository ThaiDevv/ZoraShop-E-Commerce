package com.example.zorashopminishopee.module.users.repository;

import com.example.zorashopminishopee.module.users.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
