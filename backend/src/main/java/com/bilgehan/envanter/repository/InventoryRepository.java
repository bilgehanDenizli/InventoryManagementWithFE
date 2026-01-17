package com.bilgehan.envanter.repository;

import com.bilgehan.envanter.model.entity.Inventory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    boolean existsByWarehouse_IdAndProduct_Id(long warehouseId,long productId);
    Inventory getByWarehouse_IdAndProduct_Id(long warehouseId,long productId);
    Inventory getInventoryByProduct_IdAndWarehouse_Id(long productId,long warehouseId);

    @Cacheable(cacheNames = "InventoryByWarehouseName",key = "#p0")
    Set<Inventory> getInventoryByWarehouse_Name(String name);

    @Cacheable(cacheNames = "InventoryByWarehouseCity",key = "#p0")
    Set<Inventory> getInventoryByWarehouse_City(String city);

    @Cacheable(cacheNames = "InventoryByWarehouseRegion",key = "#p0")
    Set<Inventory> getInventoryByWarehouse_Region(String region);

    @Cacheable(cacheNames = "InventoryByProductCategory",key = "#p0")
    Set<Inventory> getInventoryByProduct_ProductCategory_Category(String category);

    @Cacheable(cacheNames = "InventoryByProductId",key = "#p0")
    Set<Inventory> getInventoryByProduct_Id(long productId);

    @Cacheable(cacheNames = "InventoryByProductName",key = "#p0")
    Set<Inventory> getInventoryByProduct_Name(String productName);
}
