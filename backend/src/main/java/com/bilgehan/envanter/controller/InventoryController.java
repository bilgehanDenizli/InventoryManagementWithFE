package com.bilgehan.envanter.controller;

import com.bilgehan.envanter.model.dto.InventoryDto;
import com.bilgehan.envanter.model.request.*;
import com.bilgehan.envanter.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/addOrTakeProduct")
    public ResponseEntity<Void> addProduct(@RequestBody AddProductToInventoryRequest request) {
        inventoryService.addProduct(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deleteProduct")
    public ResponseEntity<Void> deleteProduct(@RequestBody DeleteFromInventoryRequest request) {
        inventoryService.deleteProduct(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/byWarehouseName")
    @Transactional(readOnly = true)
    public ResponseEntity<Set<InventoryDto>> getByWarehouseName(@RequestBody GetInvByWarehouseNameRequest request){
        return ResponseEntity.ok(inventoryService.getByWarehouseName(request.getWarehouseName()));
    }

    @PostMapping("/byWarehouseCity")
    public ResponseEntity<Set<InventoryDto>> getByCity(@RequestBody GetInvByCityRequest request){
        return ResponseEntity.ok(inventoryService.getByCity(request));
    }

    @PostMapping("/byWarehouseRegion")
    public ResponseEntity<Set<InventoryDto>> getByRegion(@RequestBody GetInvByRegionRequest request){
        return ResponseEntity.ok(inventoryService.getByRegion(request));
    }

    @PostMapping("/byProductCategory")
    public ResponseEntity<Set<InventoryDto>> getByProductCategory(@RequestBody GetInvByProductCategoryRequest request){
        return ResponseEntity.ok(inventoryService.getByProductCategory(request));
    }

    @PostMapping("/byProductId")
    public ResponseEntity<Set<InventoryDto>> getByProductId(@RequestBody GetInvByProductIdRequest request){
        return ResponseEntity.ok(inventoryService.getByProductId(request));
    }

    @PostMapping("/byProductName")
    public ResponseEntity<Set<InventoryDto>> getByProductName(@RequestBody GetInvByProductNameRequest request){
        return ResponseEntity.ok(inventoryService.getByProductName(request));
    }
}
