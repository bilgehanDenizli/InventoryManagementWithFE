package com.bilgehan.envanter.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class InventoryDto implements Serializable {
    private Long inventoryId;
    private List<InventoryItemDto> items;
    private WarehouseDto warehouse;
    private Timestamp updatedAt;
    private boolean isDeleted;
}
