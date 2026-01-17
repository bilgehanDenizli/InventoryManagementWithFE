package com.bilgehan.envanter.model.request;

import com.bilgehan.envanter.model.entity.ProductCategory;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateProductRequest {
    private long id;
    private String name;
    private ProductCategory productCategory;
    private boolean isDeleted;
}
