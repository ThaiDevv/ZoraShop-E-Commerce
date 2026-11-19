package com.example.zorashopminishopee.module.catagory.service.impl;

import com.example.zorashopminishopee.common.exception.BadRequestException;
import com.example.zorashopminishopee.common.exception.DuplicateResourceException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.catagory.dto.request.CreateCategoryRequest;
import com.example.zorashopminishopee.module.catagory.dto.request.UpdateCategoryRequest;
import com.example.zorashopminishopee.module.catagory.dto.response.CategoryResponse;
import com.example.zorashopminishopee.module.catagory.entity.Category;
import com.example.zorashopminishopee.module.catagory.repository.CategoryRepository;
import com.example.zorashopminishopee.module.product.repository.ProductRepository;
import com.example.zorashopminishopee.module.catagory.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
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
    private static final int MAX_LEVEL = 3;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        List<Category> categories = categoryRepository.findAllByIsActiveTrueOrderBySortOrderAsc();

        Map<Long, CategoryResponse> map = new HashMap<>();
        for (Category cat : categories) {
            map.put(cat.getId(), mapToResponse(cat));
        }

        List<CategoryResponse> rootCategories = new ArrayList<>();
        for (Category cat : categories) {
            CategoryResponse currentDto = map.get(cat.getId());
            Long parentId = cat.getParentId();
            if (parentId == null) {
                rootCategories.add(currentDto);
            } else {
                CategoryResponse parentDto = map.get(parentId);
                if (parentDto != null) {
                    parentDto.children().add(currentDto);
                }
            }
        }

        return rootCategories;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category parent;
        int level;
        if(request.parentId() == null) {
            level = 1;
            parent = null;
        }else {
             parent = categoryRepository.findById(request.parentId()).orElseThrow(
                    () -> new ResourceNotFoundException("Parent Category Not Found")
            );
             level = parent.getLevel() + 1;
             if(level > MAX_LEVEL){
                 throw new BadRequestException("Hệ thống chỉ hỗ trợ danh mục tối đa 3 cấp!");
             }
        }
        if(categoryRepository.existsBySlug(request.slug())){
            throw new DuplicateResourceException("Category slug already exists");
        }
        Category category = Category.builder()
                .parentId(request.parentId())
                .parent(parent)
                .children(new ArrayList<>())
                .name(request.name())
                .slug(request.slug())
                .iconUrl(request.iconUrl())
                .level(level)
                .sortOrder(request.sortOrder()!= null ? request.sortOrder() : 0)
                .isActive(true)
                .build();
        categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category Not Found")
        );
        if(request.name() != null){
            category.setName(request.name());
        }
        if (request.slug() != null && !request.slug().equals(category.getSlug())) {
            if (categoryRepository.existsBySlug(request.slug())) {
                throw new DuplicateResourceException("Category slug already exists: " + request.slug());
            }
            category.setSlug(request.slug());
        }
        if(request.iconUrl() != null){
            category.setIconUrl(request.iconUrl());
        }
        if (request.parentId() != null) {
            if (request.parentId() == 0) {
                category.setParent(null);
                updateLevelRecursively(category, 1);
            } else {
                if (id.equals(request.parentId())) {
                    throw new BadRequestException("Danh mục không thể chọn chính nó làm danh mục cha!");
                }

                Category newParent = categoryRepository.findById(request.parentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Parent Category Not Found with id: " + request.parentId()));
                if (isDescendant(category, newParent)) {
                    throw new BadRequestException("Không thể chọn chính nó hoặc danh mục con/cháu của nó làm danh mục cha!");
                }
                int newLevel = newParent.getLevel() + 1;
                if (newLevel + subtreeHeight(category) - 1 > MAX_LEVEL) {
                    throw new BadRequestException("Hệ thống chỉ hỗ trợ danh mục tối đa 3 cấp!");
                }
                category.setParent(newParent);
                updateLevelRecursively(category, newLevel);
            }
        }

        if (request.sortOrder() != null) {
            category.setSortOrder(request.sortOrder());
        }
        categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category Not Found with id: " + id));
        if (categoryRepository.existsByParentId(id)) {
            throw new BadRequestException("Không thể xóa danh mục này vì nó đang chứa các danh mục con! Vui lòng xóa hoặc di chuyển danh mục con trước.");
        }
        if (productRepository.existsByCategory_Id(id)) {
            throw new BadRequestException("Không thể xóa danh mục đang có sản phẩm!");
        }
        categoryRepository.delete(category);
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
    /** Number of levels in the subtree rooted at {@code category} (a leaf has height 1). */
    private int subtreeHeight(Category category) {
        int maxChildHeight = 0;
        if (category.getChildren() != null) {
            for (Category child : category.getChildren()) {
                maxChildHeight = Math.max(maxChildHeight, subtreeHeight(child));
            }
        }
        return maxChildHeight + 1;
    }

    private void updateLevelRecursively(Category category, int level) {
        category.setLevel(level);
        if (category.getChildren() != null) {
            for (Category child : category.getChildren()) {
                updateLevelRecursively(child, level + 1);
            }
        }
    }

    private boolean isDescendant(Category current, Category target) {
        Category checkNode = target;
        while (checkNode != null) {
            if (checkNode.getId().equals(current.getId())) {
                return true;
            }
            checkNode = checkNode.getParent();
        }

        return false;
    }

}

