package com.bilgehan.envanter.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
public class InventoryHistoryDto implements Serializable {
    private long id;
    private long productId;
    private long warehouseId;
    private long amountChange;
    private Timestamp createdAt;
}
