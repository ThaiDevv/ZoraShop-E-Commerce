package com.example.zorashopminishopee.module.product.service.impl;

import com.example.zorashopminishopee.common.exception.BadRequestException;
import com.example.zorashopminishopee.common.exception.DuplicateResourceException;
import com.example.zorashopminishopee.common.exception.ForbiddenException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.catagory.entity.Category;
import com.example.zorashopminishopee.module.catagory.repository.CategoryRepository;
import com.example.zorashopminishopee.module.product.dto.request.*;
import com.example.zorashopminishopee.module.product.dto.response.*;
import com.example.zorashopminishopee.module.product.emun.ProductSortBy;
import com.example.zorashopminishopee.module.product.emun.ProductSortDir;
import com.example.zorashopminishopee.module.product.entity.Inventory;
import com.example.zorashopminishopee.module.product.entity.Product;
import com.example.zorashopminishopee.module.product.entity.ProductImage;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.repository.InventoryRepository;
import com.example.zorashopminishopee.module.product.repository.ProductImageRepository;
import com.example.zorashopminishopee.module.product.repository.ProductRepository;
import com.example.zorashopminishopee.module.product.repository.ProductVariantRepository;
import com.example.zorashopminishopee.module.product.service.InventoryService;
import com.example.zorashopminishopee.module.product.service.ProductService;
import com.example.zorashopminishopee.module.product.specification.ProductSpecification;
import com.example.zorashopminishopee.module.users.entity.Shops;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.repository.ShopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryService  inventoryService;
    public String createSlug(String name) {
        String slug = name.replaceAll("Đ", "D").replaceAll("đ", "d");
        slug = Normalizer
                .normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        return slug;
    }
    public void checkSku(CreateProductRequest request) {
        List<String> skus = request.variants().stream().map(
                CreateProductVariantRequest::sku
        ).toList();
        Set<String> skuSet = new HashSet<>(skus);
        if (skuSet.size() < skus.size()) {
            throw new BadRequestException("Có mã SKU bị trùng lặp ngay trong danh sách phân loại hàng!");
        }
        if(productVariantRepository.existsBySkuIn(
                skus
        )) {
            throw new DuplicateResourceException("Có mã SKU trong danh sách đã tồn tại trên hệ thống!");
        }
    }
    public ShopSummaryResponse mapToShopSummary(Shops shops) {
        return new ShopSummaryResponse(
                shops.getId(),
                shops.getName(),
                shops.getLogoUrl()
        );
    }
    public CategorySummaryResponse mapToCategorySummary(Category category) {
        return  new CategorySummaryResponse(
                category.getId(),
                category.getName(),
                category.getSlug()
        );
    }
    public List<ProductImageResponse> mapToProductImageResponse(List<ProductImage> productImages) {
        return productImages.stream().map( ProductImage -> new ProductImageResponse(
                ProductImage.getId(),
                ProductImage.getImageUrl(),
                ProductImage.getSortOrder(),
                ProductImage.getIsPrimary()
        )).toList();
    }
    public List<ProductVariantResponse> mapToProductVariantResponse(List<ProductVariant> productVariants) {
        return productVariants.stream().map(
                ProductVariant ->
                        new ProductVariantResponse(
                                ProductVariant.getId(),
                                ProductVariant.getVariantName(),
                                ProductVariant.getSku(),
                                ProductVariant.getPrice(),
                                ProductVariant.getStock(),
                                ProductVariant.getImageUrl()
                        )
        ).toList();
    }
    public ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getSoldCount(),
                product.getRatingAvg(),
                product.getRatingCount(),
                product.getViewCount(),
                product.getStatus(),
                product.getCreatedDate(),
                mapToShopSummary(product.getShop()),
                mapToCategorySummary(product.getCategory()),
                mapToProductImageResponse(product.getImages()),
                mapToProductVariantResponse(product.getVariants())
        );
    }
    @Override
    @Transactional
    public ProductResponse createProduct(String email, CreateProductRequest request) {
        Shops shop = shopRepository.findByUser_Email(email).orElseThrow(
                () -> new ResourceNotFoundException("Shop not found"));
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category not found")
        );
        checkSku(request);
        String slug = createSlug(request.name());
        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        if (category.getLevel() != 3) {
            throw new BadRequestException("Sản phẩm phải thuộc danh mục Cấp 3!");
        }
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .category(category)
                .shop(shop)
                .slug(slug)
                .price(request.price())
                .images(new ArrayList<>())
                .variants(new ArrayList<>())
                .originalPrice(request.originalPrice())
                .build();
        if (request.images() != null) {
            for (CreateProductImageRequest imgReq : request.images()) {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(imgReq.imageUrl())
                        .sortOrder(imgReq.sortOrder() != null ? imgReq.sortOrder() : 0)
                        .isPrimary(imgReq.isPrimary() != null ? imgReq.isPrimary() : false)
                        .build();

                product.getImages().add(image);
            }
        }
        if (request.variants() != null) {
            for(CreateProductVariantRequest variantReq : request.variants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(product)
                        .variantName(variantReq.variantName())
                        .sku(variantReq.sku())
                        .price(variantReq.price())
                        .stock(variantReq.stock())
                        .imageUrl(variantReq.imageUrl())
                        .build();
                product.getVariants().add(variant);
            }
        }
        productRepository.save(product);
        if(product.getVariants() != null) {
            for( ProductVariant variant : product.getVariants()) {
                Inventory inventory = inventoryService.createInventory(variant, variant.getStock());
                variant.setInventory(inventory);
            }
        }
        return mapToResponse(product);
    }
    private String getPrimaryImageUrl(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(images.get(0).getImageUrl());
    }

    public ProductSummaryResponse mapToProductSummaryResponse(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getPrice(),
                product.getOriginalPrice(),
               getPrimaryImageUrl(product.getImages()),
                product.getRatingAvg(),
                product.getRatingCount(),
                product.getSoldCount(),
                product.getShop().getName()
        );
    }
    @Override
    public Page<ProductSummaryResponse> getAllProducts(FilterSortRequest request, int page, int size) {
        String fieldName = ProductSortBy.getValidFieldName(request.sortBy() != null ? request.sortBy().name() : null);

        Sort.Direction direction = (request.sortDir() == ProductSortDir.ASC)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, fieldName));
        Specification<Product> spec = Specification
                .where(ProductSpecification.keywordContains(request.keyword()))
                .and(ProductSpecification.hasCategory(request.categoryId()))
                .and(ProductSpecification.priceBetween(request.minPrice(), request.maxPrice()))
                .and((root, query, cb)
                        -> cb.equal(root.get("status"), "ACTIVE"));
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(this::mapToProductSummaryResponse);
    }

    @Override
    @Transactional
    public ProductResponse getProduct(String slug) {
        Product product = productRepository.findBySlug(slug).orElseThrow(
                () -> new ResourceNotFoundException("Product not found!")
        );
        long currentViews = product.getViewCount() != null ? product.getViewCount() : 0L;
        product.setViewCount(currentViews + 1);
        productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(String email, String slug, UpdateProductRequest request) {
        Shops shop = shopRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with email: " + email));

        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));

        if (!product.getShop().getId().equals(shop.getId())) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa sản phẩm của Shop khác!");
        }

        if (request.name() != null && !request.name().isBlank()) {
            product.setName(request.name());
            String newSlug = createSlug(request.name());
            if (!newSlug.equals(product.getSlug()) && productRepository.existsBySlug(newSlug)) {
                newSlug = newSlug + "-" + UUID.randomUUID().toString().substring(0, 8);
            }
            product.setSlug(newSlug);
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.originalPrice() != null) {
            product.setOriginalPrice(request.originalPrice());
        }
        if (request.status() != null) {
            product.setStatus(request.status());
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            if (category.getLevel() != 3) {
                throw new BadRequestException("Sản phẩm phải thuộc danh mục Cấp 3!");
            }
            product.setCategory(category);
        }

        if (request.images() != null && !request.images().isEmpty()) {
            List<Long> existingImageIds = request.images().stream()
                    .map(UpdateProductImageRequest::id)
                    .filter(Objects::nonNull)
                    .toList();

            Map<Long, ProductImage> imageMap = new HashMap<>();
            if (!existingImageIds.isEmpty()) {
                productImageRepository.findAllById(existingImageIds)
                        .forEach(img -> imageMap.put(img.getId(), img));
            }

            for (UpdateProductImageRequest imgReq : request.images()) {
                if (imgReq.id() != null) {
                    ProductImage existingImg = imageMap.get(imgReq.id());
                    if (existingImg != null) {
                        if (imgReq.imageUrl() != null) existingImg.setImageUrl(imgReq.imageUrl());
                        if (imgReq.sortOrder() != null) existingImg.setSortOrder(imgReq.sortOrder());
                        if (imgReq.isPrimary() != null) existingImg.setIsPrimary(imgReq.isPrimary());
                    }
                } else {
                    ProductImage newImg = ProductImage.builder()
                            .product(product)
                            .imageUrl(imgReq.imageUrl())
                            .sortOrder(imgReq.sortOrder() != null ? imgReq.sortOrder() : 0)
                            .isPrimary(imgReq.isPrimary() != null ? imgReq.isPrimary() : false)
                            .build();
                    product.getImages().add(newImg);
                }
            }
        }

        if (request.variants() != null && !request.variants().isEmpty()) {
            List<Long> existingVariantIds = request.variants().stream()
                    .map(UpdateProductVariantRequest::id)
                    .filter(Objects::nonNull)
                    .toList();

            Map<Long, ProductVariant> variantMap = new HashMap<>();
            if (!existingVariantIds.isEmpty()) {
                productVariantRepository.findAllById(existingVariantIds)
                        .forEach(v -> variantMap.put(v.getId(), v));
            }

            for (UpdateProductVariantRequest varReq : request.variants()) {
                if (varReq.id() != null) {
                    ProductVariant existingVar = variantMap.get(varReq.id());
                    if (existingVar != null) {
                        if (varReq.variantName() != null) existingVar.setVariantName(varReq.variantName());
                        if (varReq.price() != null) existingVar.setPrice(varReq.price());
                        if (varReq.stock() != null) {
                            inventoryService.updateStock(existingVar, varReq.stock());
                            existingVar.setStock(varReq.stock());
                        };
                        if (varReq.imageUrl() != null) existingVar.setImageUrl(varReq.imageUrl());
                        if (varReq.sku() != null && !varReq.sku().equals(existingVar.getSku())) {
                            if (productVariantRepository.existsBySkuIn(List.of(varReq.sku()))) {
                                throw new DuplicateResourceException("Mã SKU đã tồn tại: " + varReq.sku());
                            }
                            existingVar.setSku(varReq.sku());
                        }
                    }
                } else {
                    if (varReq.sku() != null && productVariantRepository.existsBySkuIn(List.of(varReq.sku()))) {
                        throw new DuplicateResourceException("Mã SKU đã tồn tại: " + varReq.sku());
                    }
                    ProductVariant newVar = ProductVariant.builder()
                            .product(product)
                            .variantName(varReq.variantName())
                            .sku(varReq.sku())
                            .price(varReq.price())
                            .stock(varReq.stock())
                            .imageUrl(varReq.imageUrl())
                            .build();
                    productVariantRepository.save(newVar);
                    Inventory inventory = inventoryService.createInventory(newVar, newVar.getStock());
                    newVar.setInventory(inventory);
                    product.getVariants().add(newVar);
                }
            }
        }

        productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String email, String slug) {
        Shops shop = shopRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with email: " + email));

        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));

        if (!product.getShop().getId().equals(shop.getId())) {
            throw new ForbiddenException("Bạn không có quyền XÓA sản phẩm của Shop khác!");
        }
        productRepository.delete(product);
        shop.getProducts().remove(product);
    }

    @Override
    public Page<ProductSummaryResponse> getMyProducts(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Shops shop = shopRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with email: " + email));
        Page<Product> products = productRepository.findByShop(shop, pageable);
        return products.map(this::mapToProductSummaryResponse);
    }

}

