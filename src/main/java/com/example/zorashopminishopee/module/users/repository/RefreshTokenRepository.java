package com.example.zorashopminishopee.module.users.repository;

import com.example.zorashopminishopee.module.users.entity.RefreshToken;
import com.example.zorashopminishopee.module.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByJti(String jti);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshToken rf SET rf.isRevoked = true " +
            "WHERE rf.jti = :jti " +
            "AND rf.isRevoked = false " +
            "AND rf.expirationDate > :now")
    int revokeJti(@Param("jti") String jti, @Param("now")Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshToken rf SET rf.isRevoked = true " +
            "WHERE rf.user = :user " +
            "AND rf.isRevoked = false")
    int revokeAllJti(@Param("user") Users user);
}
