package com.example.zorashopminishopee.module.users.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.module.users.dto.request.CreateShopRequest;
import com.example.zorashopminishopee.module.users.dto.request.UpdateShopRequire;
import com.example.zorashopminishopee.module.users.dto.response.ShopResponse;
import com.example.zorashopminishopee.module.users.service.ShopService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @PostMapping("/shops")
    public ResponseEntity<ApiResponse<ShopResponse>> createShop(Authentication authentication,
                                                                @Valid @RequestBody CreateShopRequest request) {
        ShopResponse response = shopService.createShop(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/shops/{id}")
    public ResponseEntity<ApiResponse<ShopResponse>> getShop(@PathVariable Long id) {
        ShopResponse response = shopService.getShop(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @PutMapping("/shops/{id}")
    public ResponseEntity<ApiResponse<ShopResponse>> updateShop(Authentication authentication,
                                                                @PathVariable Long id, @Valid @RequestBody UpdateShopRequire request) {
        ShopResponse response = shopService.updateShop(authentication.getName(),id,request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
