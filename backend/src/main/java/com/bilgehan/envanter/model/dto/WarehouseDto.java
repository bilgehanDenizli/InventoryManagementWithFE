package com.bilgehan.envanter.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class WarehouseDto implements Serializable {
    private long id;
    private String name;
    private String city;
    private String region;
}
