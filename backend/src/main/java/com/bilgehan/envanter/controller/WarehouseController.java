package com.bilgehan.envanter.controller;

import com.bilgehan.envanter.model.dto.WarehouseDto;
import com.bilgehan.envanter.model.request.GetWarehousesRequest;
import com.bilgehan.envanter.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/")
    public ResponseEntity<List<WarehouseDto>> getWarehouses(@RequestBody GetWarehousesRequest request){
        return ResponseEntity.ok(warehouseService.getWarehouses(request));
    }
}
