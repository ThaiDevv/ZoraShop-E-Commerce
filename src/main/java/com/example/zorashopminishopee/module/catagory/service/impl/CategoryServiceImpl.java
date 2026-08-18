package com.example.zorashopminishopee.module.catagory.service.impl;

import com.example.zorashopminishopee.module.catagory.dto.CategoryResponse;
import com.example.zorashopminishopee.module.catagory.entity.Category;
import com.example.zorashopminishopee.module.catagory.repository.CategoryRepository;
import com.example.zorashopminishopee.module.catagory.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        List<Category> categories = categoryRepository.findAllByIsActiveTrueOrderBySortOrderAsc();

        // 1. Map tất cả Entity sang DTO
        Map<Long, CategoryResponse> map = new HashMap<>();
        for (Category cat : categories) {
            map.put(cat.getId(), mapToResponse(cat));
        }

        // 2. Ghép con vào cha
        List<CategoryResponse> rootCategories = new ArrayList<>();
        for (Category cat : categories) {
            CategoryResponse currentDto = map.get(cat.getId());
            Long parentId = cat.getParent() != null ? cat.getParent().getId() : null;
            if (parentId == null) {
                // Là danh mục Cấp 1 -> Đưa vào danh sách gốc
                rootCategories.add(currentDto);
            } else {
                // Là danh mục con -> Nhét vào 'children' của danh mục cha tương ứng
                CategoryResponse parentDto = map.get(parentId);
                if (parentDto != null) {
                    parentDto.children().add(currentDto);
                }
            }
        }

        return rootCategories; // Trả về danh sách Cấp 1 đã bao gồm tất cả các cấp con bên trong
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .iconUrl(category.getIconUrl())
                .level(category.getLevel())
                .sortOrder(category.getSortOrder())
                .children(new ArrayList<>())
                .build();
    }
}

