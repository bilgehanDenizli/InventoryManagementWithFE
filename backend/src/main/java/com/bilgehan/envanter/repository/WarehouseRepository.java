package com.bilgehan.envanter.repository;

import com.bilgehan.envanter.model.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    Warehouse getWarehouseById(long warehouseId);

    @Query(nativeQuery = true,value = "select * from warehouse order by id")
    Page<Warehouse> findAllWithPagingOrderById(PageRequest pageRequest);
}
