package com.bilgehan.envanter.service;

import com.bilgehan.envanter.model.dto.WarehouseDto;
import com.bilgehan.envanter.model.entity.Warehouse;
import com.bilgehan.envanter.model.request.GetWarehousesRequest;
import com.bilgehan.envanter.repository.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<WarehouseDto> getWarehouses(GetWarehousesRequest request) {
        List<WarehouseDto> warehouseDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit());
        Page<Warehouse> warehouseList = warehouseRepository.findAllWithPagingOrderById(pageRequest);
        for (Warehouse warehouse : warehouseList
        ) {
            WarehouseDto warehouseDto = WarehouseDto.builder()
                    .id(warehouse.getId())
                    .name(warehouse.getName())
                    .city(warehouse.getCity())
                    .region(warehouse.getRegion())
                    .build();
            warehouseDtoList.add(warehouseDto);
        }
        return warehouseDtoList;
    }

    public WarehouseDto getWarehouseById(long warehouseId) {
        Warehouse warehouse = warehouseRepository.getWarehouseById(warehouseId);
        return WarehouseDto.builder()
                .id(warehouseId)
                .city(warehouse.getCity())
                .name(warehouse.getName())
                .region(warehouse.getRegion())
                .build();
    }
}
