package com.bilgehan.envanter.service;

import com.bilgehan.envanter.controller.converter.Converter;
import com.bilgehan.envanter.model.dto.WarehouseDto;
import com.bilgehan.envanter.model.entity.Warehouse;
import com.bilgehan.envanter.model.request.GetWarehousesRequest;
import com.bilgehan.envanter.repository.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final Converter converter;

    public WarehouseService(WarehouseRepository warehouseRepository, Converter converter) {
        this.warehouseRepository = warehouseRepository;
        this.converter = converter;
    }

    public List<WarehouseDto> getWarehouses(GetWarehousesRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit());
        Page<Warehouse> warehouseList = warehouseRepository.findAllWithPagingOrderById(pageRequest);
        return converter.mapWarehouseDtoList(warehouseList.stream().collect(Collectors.toSet()));
    }

    public WarehouseDto getWarehouseById(long warehouseId) {
        Warehouse warehouse = warehouseRepository.getWarehouseById(warehouseId);
        return converter.mapWarehouseDto(warehouse);
    }
}
