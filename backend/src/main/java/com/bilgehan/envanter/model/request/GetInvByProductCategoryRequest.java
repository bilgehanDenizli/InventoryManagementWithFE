package com.bilgehan.envanter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetInvByProductCategoryRequest {
    public String category;
}
