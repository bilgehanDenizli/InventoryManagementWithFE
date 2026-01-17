package com.bilgehan.envanter.model.dto;

import com.bilgehan.envanter.model.entity.ProductCategory;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ProductDto implements Serializable {
    private long id;
    private String name;
    private ProductCategory productCategory;
}
