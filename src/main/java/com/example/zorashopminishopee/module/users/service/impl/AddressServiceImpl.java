package com.example.zorashopminishopee.module.users.service.impl;

import com.example.zorashopminishopee.common.exception.AddressLimitExceededException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.users.dto.request.CreateAddressRequest;
import com.example.zorashopminishopee.module.users.dto.response.AddressResponse;
import com.example.zorashopminishopee.module.users.entity.Address;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.repository.AddressRepository;
import com.example.zorashopminishopee.module.users.repository.UserRepository;
import com.example.zorashopminishopee.module.users.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public AddressResponse createAddress(String email, CreateAddressRequest request) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        long count = addressRepository.countByUserId(user.getId());
        if (count >= 10) {
            throw new AddressLimitExceededException("User can have a maximum of 10 addresses");
        }

        boolean shouldBeDefault = Boolean.TRUE.equals(request.isDefault()) || count == 0;
        if (shouldBeDefault) {
            addressRepository.findByUserEmailAndIsDefaultTrue(email)
                    .ifPresent(oldDefault -> oldDefault.setIsDefault(false));
        }

        Address address = Address.builder()
                .fullName(request.fullName())
                .phone(request.phone())
                .street(request.street())
                .ward(request.ward())
                .district(request.district())
                .city(request.city())
                .isDefault(shouldBeDefault)
                .user(user)
                .build();

        addressRepository.save(address);

        return mapToResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(String email) {
        return addressRepository.findAllByUserEmail(email).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    @Override
    public AddressResponse updateAddress(String email, Long addressId, CreateAddressRequest request) {
        Address address = addressRepository.findByIdAndUserEmail(addressId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (request.fullName() != null) {
            address.setFullName(request.fullName());
        }
        if (request.phone() != null) {
            address.setPhone(request.phone());
        }
        if (request.street() != null) {
            address.setStreet(request.street());
        }
        if (request.ward() != null) {
            address.setWard(request.ward());
        }
        if (request.district() != null) {
            address.setDistrict(request.district());
        }
        if (request.city() != null) {
            address.setCity(request.city());
        }
        if (Boolean.TRUE.equals(request.isDefault())) {
            addressRepository.findByUserEmailAndIsDefaultTrue(email)
                    .ifPresent(oldDefault -> oldDefault.setIsDefault(false));
            address.setIsDefault(true);
        }

        addressRepository.save(address);
        return mapToResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(String email, Long addressId) {
        Address address = addressRepository.findByIdAndUserEmail(addressId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);
    }


    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddress(String email, Long addressId) {
        Address address = addressRepository.findByIdAndUserEmail(addressId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return mapToResponse(address);
    }

    @Transactional
    @Override
    public AddressResponse setDefaultAddress(String email, Long addressId) {
        Address address = addressRepository.findByIdAndUserEmail(addressId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressRepository.findByUserEmailAndIsDefaultTrue(email)
                .ifPresent(oldDefault -> oldDefault.setIsDefault(false));

        address.setIsDefault(true);
        addressRepository.save(address);

        return mapToResponse(address);
    }

    private AddressResponse mapToResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getFullName(),
                address.getPhone(),
                address.getStreet(),
                address.getWard(),
                address.getDistrict(),
                address.getCity(),
                address.getIsDefault()
        );
    }
}
