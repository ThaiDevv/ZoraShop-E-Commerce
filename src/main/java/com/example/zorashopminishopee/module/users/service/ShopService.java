package com.example.zorashopminishopee.module.users.service;

import com.example.zorashopminishopee.module.users.dto.request.CreateShopRequest;
import com.example.zorashopminishopee.module.users.dto.request.UpdateShopRequire;
import com.example.zorashopminishopee.module.users.dto.response.ShopResponse;

public interface ShopService {
    public ShopResponse createShop(String email, CreateShopRequest createShopRequest);
    public ShopResponse getShop(Long id);
    public ShopResponse updateShop(String email, Long id, UpdateShopRequire updateShopRequire);
}
