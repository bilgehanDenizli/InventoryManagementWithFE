package com.bilgehan.envanter.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryItemDto {
    private ProductDto product;
    private long quantity;
}
