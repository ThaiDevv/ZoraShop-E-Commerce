package com.example.zorashopminishopee.module.users.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.module.users.dto.request.CreateAddressRequest;
import com.example.zorashopminishopee.module.users.dto.response.AddressResponse;
import com.example.zorashopminishopee.module.users.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(Authentication authentication) {
        List<AddressResponse> addresses = addressService.getAddresses(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            Authentication authentication,
            @Valid @RequestBody CreateAddressRequest request
    ) {
        AddressResponse address = addressService.createAddress(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            Authentication authentication,
            @PathVariable Long addressId,
            @Valid @RequestBody CreateAddressRequest request
    ) {
        AddressResponse address = addressService.updateAddress(authentication.getName(), addressId, request);
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            Authentication authentication,
            @PathVariable Long addressId
    ) {
        addressService.deleteAddress(authentication.getName(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }

    @PutMapping("/addresses/{addressId}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(
            Authentication authentication,
            @PathVariable Long addressId
    ) {
        AddressResponse addressResponse = addressService.setDefaultAddress(authentication.getName(), addressId);
        return ResponseEntity.ok(ApiResponse.success(addressResponse));
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddress(
            Authentication authentication,
            @PathVariable Long addressId
    ) {
        AddressResponse addressResponse = addressService.getAddress(authentication.getName(), addressId);
        return ResponseEntity.ok(ApiResponse.success(addressResponse));
    }
}
