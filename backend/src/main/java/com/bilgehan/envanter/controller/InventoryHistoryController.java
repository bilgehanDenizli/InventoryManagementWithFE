package com.bilgehan.envanter.controller;

import com.bilgehan.envanter.model.dto.InventoryHistoryDto;
import com.bilgehan.envanter.model.request.GetHistoryRequest;
import com.bilgehan.envanter.service.InventoryHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/inventoryHistory")
public class InventoryHistoryController {
    private final InventoryHistoryService inventoryHistoryService;

    public InventoryHistoryController(InventoryHistoryService inventoryHistoryService) {
        this.inventoryHistoryService = inventoryHistoryService;
    }

    @PostMapping("/")
    public ResponseEntity<List<InventoryHistoryDto>> getHistory(@RequestBody GetHistoryRequest getHistoryRequest) {
        return ResponseEntity.ok(inventoryHistoryService.getHistory(getHistoryRequest));
    }

    @PostMapping("/batch-insert")
    public ResponseEntity<Void> insertHistory() {
        inventoryHistoryService.fireBatchEvent();
        return ResponseEntity.ok().build();
    }
}
