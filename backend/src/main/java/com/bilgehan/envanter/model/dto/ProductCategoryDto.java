package com.bilgehan.envanter.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ProductCategoryDto implements Serializable {
    private long id;
    private String category;
}
