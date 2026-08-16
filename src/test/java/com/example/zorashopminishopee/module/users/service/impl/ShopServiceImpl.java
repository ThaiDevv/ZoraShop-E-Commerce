package com.example.zorashopminishopee.module.users.service.impl;

import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.users.dto.request.CreateShopRequest;
import com.example.zorashopminishopee.module.users.dto.response.ShopResponse;
import com.example.zorashopminishopee.module.users.entity.Shops;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.enums.UserRole;
import com.example.zorashopminishopee.module.users.repository.ShopRepository;
import com.example.zorashopminishopee.module.users.repository.UserRepository;
import com.example.zorashopminishopee.module.users.service.ShopService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShopServiceImpl implements ShopService {
    private ShopRepository shopRepository;
    private UserRepository userRepository;
    @Override
    @Transactional
    public ShopResponse createShop(String email, CreateShopRequest request) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        if (user.getShops() != null) {
            throw new ResourceNotFoundException("User already has a shop");
        }
        user.setRole(UserRole.SELLER);
        Shops shops = Shops.builder()
                .user(user)
                .name(request.name())
                .description(request.description())
                .logoUrl("")
                .bannerUrl("")
                .rating((double) 0)
                .totalProducts(0)
                .totalFollowers(0)
                .isActive(true)
                .build();
        user.setShops(shops);
        return new ShopResponse(
                shops.getName(),
                shops.getDescription(),
                shops.getLogoUrl(),
                shops.getBannerUrl(),
                shops.getRating(),
                shops.getTotalProducts(),
                shops.getTotalFollowers(),
                shops.getIsActive()
        );
    }

}
