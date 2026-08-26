package com.example.zorashopminishopee.module.product.controller;


import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.common.dto.PageResponse;
import com.example.zorashopminishopee.module.product.dto.response.InventoryLogResponse;
import com.example.zorashopminishopee.module.product.entity.InventoryLog;
import com.example.zorashopminishopee.module.product.service.InventoryLogService;
import com.example.zorashopminishopee.module.product.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seller/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerInventoryController {

    private final InventoryService inventoryService;
    private final InventoryLogService inventoryLogService;
    @PutMapping("/{variantId}")
    public ResponseEntity<ApiResponse<Void>> updateStock(
            Authentication authentication,
            @PathVariable Long variantId,
            @RequestParam int quantity
    ) {
        inventoryService.updateStockApi(authentication.getName(), variantId, quantity);
        return ResponseEntity.ok(ApiResponse.success());
    }
    @GetMapping("/{variantId}/logs")
    public ResponseEntity<ApiResponse<PageResponse<InventoryLogResponse>>> getInventoryLogs(Authentication authentication,
                                                                                            @PathVariable Long variantId,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "20") int size) {
        Page<InventoryLogResponse> inventoryLogResponses = inventoryLogService.getInventoryLogs(authentication.getName(), variantId, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(inventoryLogResponses)));
    }
}

