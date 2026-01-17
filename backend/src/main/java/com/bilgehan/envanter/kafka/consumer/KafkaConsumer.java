package com.bilgehan.envanter.kafka.consumer;

import com.bilgehan.envanter.model.entity.Inventory;
import com.bilgehan.envanter.model.entity.InventoryHistory;
import com.bilgehan.envanter.model.kafka.WarehouseCache;
import com.bilgehan.envanter.service.InventoryHistoryService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final HazelcastInstance hazelcastInstance;
    private final InventoryHistoryService inventoryHistoryService;
    private final List<InventoryHistory> buffer = new ArrayList<>();
    @Value("${batch.inventory-history.size}")
    private final int BATCH_SIZE = 10;
    private final long FLUSH_INTERVAL_MS = 1000 * 60;
    private Instant lastFlush = Instant.now();


    @KafkaListener(topics = "inventory-cache-delete", groupId = "group-1")
    public void consumeDeleteCaches(List<String> cacheNames) {
        cacheNames.forEach(this::deleteCache);
    }

    @KafkaListener(topics = "inventory-cache-delete-by-name", groupId = "group-1")
    public void consumeDeleteCache(String cacheName) {
        deleteCache(cacheName);
    }

    @KafkaListener(topics = "inventory-cache-delete-by-warehouse-name", groupId = "group-1")
    public void consumeDeleteCacheByWarehouseName(WarehouseCache warehouseCache) {
        warehouseCache.getCacheName().forEach(cacheName -> {
            deleteCacheByWarehouseName(cacheName, warehouseCache.getWarehouseName());
        });
    }

    @KafkaListener(topics = "inventory-history-log-batch", groupId = "group-1", containerFactory = "manualAckKafkaListenerContainerFactory")
    @Transactional
    public void consumeInventoryHistoryLogBatch(List<ConsumerRecord<String, InventoryHistory>> records, Acknowledgment ack) {
        if (records.isEmpty()) return;
        try {
            boolean timeReached = Duration.between(lastFlush, Instant.now()).toMillis() >= FLUSH_INTERVAL_MS;
            if (records.size() >= BATCH_SIZE || timeReached) {
                records.stream()
                        .map(ConsumerRecord::value)
                        .forEach(buffer::add);

                inventoryHistoryService.insertBatchInventoryHistory(new ArrayList<>(buffer));
                ack.acknowledge();
                buffer.clear();
                lastFlush = Instant.now();

            }
        } catch (Exception e) {
            log.error("Failed to process batch", e);
        }

    }

    private void deleteCacheByWarehouseName(String cacheName, String warehouseName) {
        IMap<Integer, String> map = hazelcastInstance.getMap(cacheName);
        map.remove(warehouseName);
        log.info("Deleting cache name {} and warehouse name {}", cacheName, warehouseName);
    }

    public void deleteCache(String cacheName) {
        IMap<Integer, Inventory> map = hazelcastInstance.getMap(cacheName);
        map.evictAll();
        log.info("Deleting cache name {}", cacheName);
    }
}
