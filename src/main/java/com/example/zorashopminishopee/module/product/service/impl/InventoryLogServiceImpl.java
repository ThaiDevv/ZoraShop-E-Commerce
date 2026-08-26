package com.example.zorashopminishopee.module.product.service.impl;

import com.example.zorashopminishopee.common.exception.ForbiddenException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.product.dto.response.InventoryLogResponse;
import com.example.zorashopminishopee.module.product.entity.InventoryLog;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.repository.InventoryLogRepository;
import com.example.zorashopminishopee.module.product.repository.ProductRepository;
import com.example.zorashopminishopee.module.product.repository.ProductVariantRepository;
import com.example.zorashopminishopee.module.product.service.InventoryLogService;
import com.example.zorashopminishopee.module.users.entity.Shops;
import com.example.zorashopminishopee.module.users.repository.ShopRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InventoryLogServiceImpl implements InventoryLogService {
    private final InventoryLogRepository inventoryLogRepository;
    private final ShopRepository shopRepository;
    private final ProductVariantRepository productVariantRepository;
    @Override
    public Page<InventoryLogResponse> getInventoryLogs(String email, Long variantId, int page, int size) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
        Shops shop = shopRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        if (!productVariant.getProduct().getShop().getId().equals(shop.getId())) {
            throw new ForbiddenException("Bạn không có quyền xem inventoryLog của Shop khác!");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<InventoryLog> inventoryLogPage = inventoryLogRepository.findByInventory_Variant_Id(variantId, pageable);
        return inventoryLogPage.map(
                InventoryLog -> {
                    return new InventoryLogResponse(
                            InventoryLog.getId(),
                            InventoryLog.getInventory().getId(),
                            InventoryLog.getType(),
                            InventoryLog.getQuantityChange(),
                            InventoryLog.getQuantityAfter(),
                            InventoryLog.getReason(),
                            InventoryLog.getReferenceId(),
                            InventoryLog.getCreatedAt()
                    );
                }
        );
    }
}
