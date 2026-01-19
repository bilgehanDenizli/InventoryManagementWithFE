package com.bilgehan.envanter.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum FilterType {
    WAREHOUSE_NAME,
    WAREHOUSE_CITY,
    WAREHOUSE_REGION,
    PRODUCT_NAME,
    PRODUCT_ID,
    PRODUCT_CATEGORY;

    @JsonValue
    public String toJson() {
        return name().toUpperCase(Locale.ROOT);
    }
}
