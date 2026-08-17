package com.example.zorashopminishopee.module.users.repository;

import com.example.zorashopminishopee.module.users.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    long countByUserId(Long id);
    List<Address> findAllByUserEmail(String email);
    Optional<Address> findByUserEmailAndIsDefaultTrue(String email);;
    Optional<Address> findByIdAndUserEmail(
            Long addressId,
            String email
    );}
