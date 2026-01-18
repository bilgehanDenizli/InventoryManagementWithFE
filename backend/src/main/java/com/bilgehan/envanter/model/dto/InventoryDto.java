package com.bilgehan.envanter.model.dto;

import com.bilgehan.envanter.model.entity.Product;
import com.bilgehan.envanter.model.entity.Warehouse;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
public class InventoryDto implements Serializable {
    private Long inventoryId;
    private long amount;
    private ProductDto product;
    private WarehouseDto warehouse;
    private Timestamp updatedAt;
    private boolean isDeleted;
}
