package com.example.zorashopminishopee.module.users.repository;

import com.example.zorashopminishopee.module.users.entity.Shops;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shops, Long> {
}
