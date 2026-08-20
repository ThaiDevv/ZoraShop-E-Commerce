package com.example.zorashopminishopee.module.users.repository;

import com.example.zorashopminishopee.module.users.entity.Shops;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shops, Long> {
    Optional<Shops> findByUser_Email(String email);

}
