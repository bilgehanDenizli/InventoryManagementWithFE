package com.bilgehan.envanter.model.kafka;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseCache {
    private String warehouseName;
    private List<String> cacheName;
}
