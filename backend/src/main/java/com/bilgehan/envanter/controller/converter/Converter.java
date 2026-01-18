package com.bilgehan.envanter.controller.converter;

import com.bilgehan.envanter.model.dto.InventoryDto;
import com.bilgehan.envanter.model.dto.ProductCategoryDto;
import com.bilgehan.envanter.model.dto.ProductDto;
import com.bilgehan.envanter.model.dto.WarehouseDto;
import com.bilgehan.envanter.model.entity.Inventory;
import com.bilgehan.envanter.model.entity.Product;
import com.bilgehan.envanter.model.entity.ProductCategory;
import com.bilgehan.envanter.model.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Converter {

    public Set<InventoryDto> mapInventoryDtos(Set<Inventory> inventorySet) {
        return inventorySet.stream()
                .map(this::mapInventoryDto).
                collect(Collectors.toSet());
    }

    public InventoryDto mapInventoryDto(Inventory inventory) {
        return InventoryDto.builder()
                .inventoryId(inventory.getId())
                .amount(inventory.getAmount())
                .product(mapProductDto(inventory.getProduct()))
                .warehouse(mapWarehouseDto(inventory.getWarehouse()))
                .updatedAt(inventory.getUpdatedAt())
                .isDeleted(inventory.isDeleted())
                .build();
    }

    public ProductDto mapProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .productCategory(mapProductCategoryDto(product.getProductCategory()))
                .name(product.getName())
                .build();
    }

    public ProductCategoryDto mapProductCategoryDto(ProductCategory productCategory) {
        return ProductCategoryDto.builder()
                .id(productCategory.getId())
                .category(productCategory.getCategory())
                .build();
    }

    public WarehouseDto mapWarehouseDto(Warehouse warehouse) {
        return WarehouseDto.builder()
                .id(warehouse.getId())
                .city(warehouse.getCity())
                .name(warehouse.getName())
                .region(warehouse.getRegion())
                .build();
    }
}
