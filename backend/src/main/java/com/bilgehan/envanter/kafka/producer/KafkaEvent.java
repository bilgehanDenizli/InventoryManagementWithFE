package com.bilgehan.envanter.kafka.producer;

import com.bilgehan.envanter.model.dto.WarehouseDto;
import com.bilgehan.envanter.model.entity.InventoryHistory;
import com.bilgehan.envanter.model.kafka.WarehouseCache;
import com.bilgehan.envanter.service.WarehouseService;
import com.bilgehan.envanter.util.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KafkaEvent {
    private final KafkaProducer kafkaProducer;
    private final WarehouseService warehouseService;

    public KafkaEvent(KafkaProducer kafkaProducer, WarehouseService warehouseService) {
        this.kafkaProducer = kafkaProducer;
        this.warehouseService = warehouseService;
    }

    public void deleteCache() {
        List<String> caches = CacheConstants.getCacheNames();
        kafkaProducer.send("inventory-cache-delete", caches);
    }

    public void deleteCacheByName(String cacheName) {
        kafkaProducer.send("inventory-cache-delete-by-name", cacheName);
    }

    public void deleteCacheByWarehouseName(long warehouseId) {
        WarehouseDto warehouse = warehouseService.getWarehouseById(warehouseId);
        List<String> caches = CacheConstants.getCacheNames();
        WarehouseCache warehouseCache = WarehouseCache.builder()
                .warehouseName(warehouse.getName())
                .cacheName(caches)
                .build();
        kafkaProducer.send("inventory-cache-delete-by-warehouse-name", warehouseCache);
    }

    public void batchInventoryHistory(InventoryHistory inventoryHistory) {
        log.info("inventory History: {}", inventoryHistory);
        kafkaProducer.send("inventory-history-log-batch", inventoryHistory);
    }

}
