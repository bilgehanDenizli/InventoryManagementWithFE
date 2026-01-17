package com.bilgehan.envanter.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheConstants {
    public static final String INVENTORY_BY_WAREHOUSE_NAME = "InventoryByWarehouseName";
    public static final String INVENTORY_BY_WAREHOUSE_CITY = "InventoryByWarehouseCity";
    public static final String INVENTORY_BY_WAREHOUSE_REGION = "InventoryByWarehouseRegion";
    public static final String INVENTORY_BY_PRODUCT_CATEGORY = "InventoryByProductCategory";
    public static final String INVENTORY_BY_PRODUCT_ID = "InventoryByProductId";
    public static final String INVENTORY_BY_PRODUCT_NAME= "InventoryByProductName";

    public static List<String> getCacheNames() {
        List<String> cacheNames = new ArrayList<>();
        cacheNames.add(INVENTORY_BY_WAREHOUSE_NAME);
        cacheNames.add(INVENTORY_BY_WAREHOUSE_CITY);
        cacheNames.add(INVENTORY_BY_WAREHOUSE_REGION);
        cacheNames.add(INVENTORY_BY_PRODUCT_CATEGORY);
        cacheNames.add(INVENTORY_BY_PRODUCT_ID);
        cacheNames.add(INVENTORY_BY_PRODUCT_NAME);
        return cacheNames;
    }

}
