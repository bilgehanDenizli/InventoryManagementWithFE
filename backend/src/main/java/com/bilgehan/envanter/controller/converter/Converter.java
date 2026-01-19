package com.bilgehan.envanter.controller.converter;

import com.bilgehan.envanter.model.dto.*;
import com.bilgehan.envanter.model.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Converter {

    public Set<InventoryDto> mapInventoryDtoList(Set<Inventory> inventorySet) {
        return inventorySet.stream()
                .map(this::mapInventoryDto).
                collect(Collectors.toSet());
    }

    public InventoryDto mapInventoryDto(Inventory inventory) {
        return InventoryDto.builder()
                .inventoryId(inventory.getId())
                .items(mapInventoryItemDtoList(inventory.getInventoryItems()))
                .warehouse(mapWarehouseDto(inventory.getWarehouse()))
                .updatedAt(inventory.getUpdatedAt())
                .isDeleted(inventory.isDeleted())
                .build();
    }

    public List<InventoryItemDto> mapInventoryItemDtoList(List<InventoryItem> inventoryItemList) {
        return inventoryItemList.stream().map(this::mapInventoryItemDto).collect(Collectors.toList());
    }

    public InventoryItemDto mapInventoryItemDto(InventoryItem inventoryItem) {
        return InventoryItemDto.builder()
                .product(mapProductDto(inventoryItem.getProduct()))
                .quantity(inventoryItem.getAmount())
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

    public List<WarehouseDto> mapWarehouseDtoList(Set<Warehouse> warehouses) {
        return warehouses.stream()
                .map(this::mapWarehouseDto)
                .collect(Collectors.toList());
    }
}
