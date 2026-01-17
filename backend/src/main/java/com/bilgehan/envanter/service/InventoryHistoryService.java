package com.bilgehan.envanter.service;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.bilgehan.envanter.kafka.producer.KafkaEvent;
import com.bilgehan.envanter.model.dto.InventoryHistoryDto;
import com.bilgehan.envanter.model.entity.InventoryHistory;
import com.bilgehan.envanter.model.request.GetHistoryRequest;
import com.bilgehan.envanter.repository.InventoryHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryHistoryService {
    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final KafkaEvent kafkaEvent;

    public List<InventoryHistoryDto> getHistory(GetHistoryRequest request) {

        List<InventoryHistoryDto> inventoryHistoryDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit());
        Page<InventoryHistory> inventoryHistoryList = inventoryHistoryRepository.findAllWithPagingOrderByCreatedAtDesc(pageRequest);

        for (InventoryHistory inventoryHistory : inventoryHistoryList) {
            InventoryHistoryDto inventoryHistoryDto = InventoryHistoryDto.builder()
                    .id(inventoryHistory.getId())
                    .productId(inventoryHistory.getProductId())
                    .warehouseId(inventoryHistory.getWarehouseId())
                    .amountChange(inventoryHistory.getAmountChange())
                    .createdAt(inventoryHistory.getCreatedAt())
                    .build();
            inventoryHistoryDtoList.add(inventoryHistoryDto);
        }
        return inventoryHistoryDtoList;
    }

    public void insertBatchInventoryHistory(List<InventoryHistory> inventoryHistoryList) {
        inventoryHistoryRepository.saveAll(inventoryHistoryList);
    }

    public void fireBatchEvent() {
        for (int i = 0; i < 50; i++) {
            InventoryHistory inventoryHistory = InventoryHistory.builder()
                    .productId(1)
                    .warehouseId(1)
                    .amountChange(RandomUtil.getPositiveInt())
                    .build();
            kafkaEvent.batchInventoryHistory(inventoryHistory);
        }
    }
}
