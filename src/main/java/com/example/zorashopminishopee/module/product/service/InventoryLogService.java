package com.example.zorashopminishopee.module.product.service;

import com.example.zorashopminishopee.module.product.dto.request.FilterSortRequest;
import com.example.zorashopminishopee.module.product.dto.response.InventoryLogResponse;
import org.springframework.data.domain.Page;

public interface InventoryLogService {
    Page<InventoryLogResponse> getInventoryLogs(String email, Long variantId, int page, int size);
}
