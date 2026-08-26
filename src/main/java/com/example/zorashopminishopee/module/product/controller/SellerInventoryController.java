package com.example.zorashopminishopee.module.product.controller;


import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.module.product.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerInventoryController {

    private final InventoryService inventoryService;

    @PutMapping("/{variantId}")
    public ResponseEntity<ApiResponse<Void>> updateStock(
            Authentication authentication,
            @PathVariable Long variantId,
            @RequestParam int quantity
    ) {
        inventoryService.updateStockApi(authentication.getName(), variantId, quantity);
        return ResponseEntity.ok(ApiResponse.success());
    }
}

