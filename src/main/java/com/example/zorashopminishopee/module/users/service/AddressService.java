package com.example.zorashopminishopee.module.users.service;

import com.example.zorashopminishopee.module.users.dto.request.CreateAddressRequest;
import com.example.zorashopminishopee.module.users.dto.response.AddressResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface AddressService {
    public AddressResponse createAddress(String email, CreateAddressRequest request);
    public List<AddressResponse> getAddresses(String email);
    public AddressResponse updateAddress(String email, Long addressId, CreateAddressRequest request);
    public void deleteAddress(String email, Long addressId);
    public AddressResponse getAddress(String email, Long addressId);
    public AddressResponse setDefaultAddress(String email, Long addressId);
}
