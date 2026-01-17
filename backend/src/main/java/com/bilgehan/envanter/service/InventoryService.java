package com.bilgehan.envanter.service;

import com.bilgehan.envanter.kafka.producer.KafkaEvent;
import com.bilgehan.envanter.kafka.producer.KafkaProducer;
import com.bilgehan.envanter.model.dto.InventoryDto;
import com.bilgehan.envanter.model.entity.Inventory;
import com.bilgehan.envanter.model.entity.InventoryHistory;
import com.bilgehan.envanter.model.entity.Product;
import com.bilgehan.envanter.model.entity.Warehouse;
import com.bilgehan.envanter.model.request.*;
import com.bilgehan.envanter.repository.InventoryHistoryRepository;
import com.bilgehan.envanter.repository.InventoryRepository;
import com.bilgehan.envanter.repository.ProductRepository;
import com.bilgehan.envanter.repository.WarehouseRepository;
import com.bilgehan.envanter.exception.NotAcceptableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class InventoryService {

    Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final KafkaEvent kafkaEvent;

    public InventoryService(InventoryRepository inventoryRepository, InventoryHistoryRepository inventoryHistoryRepository, ProductRepository productRepository,
                            WarehouseRepository warehouseRepository, KafkaEvent kafkaEvent) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryHistoryRepository = inventoryHistoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.kafkaEvent = kafkaEvent;
    }


    public void addProduct(AddProductToInventoryRequest request) {

        if (request.getAmount() == 0) {
            logger.error("You cant add or take zero products.");
            throw new NotAcceptableException("You cant add or take zero products.");
        }

        //checks if product exists within the warehouse inventory
        boolean inventoryFlag = inventoryRepository.existsByWarehouse_IdAndProduct_Id(request.getWarehouseId(), request.getProductId());
        Inventory inventory;

        if (inventoryFlag) {
            //if inventory exist adds or subtracts the amount
            inventory = inventoryRepository.getByWarehouse_IdAndProduct_Id(request.getWarehouseId(), request.getProductId());
            if (inventory.getAmount() + request.getAmount() < 0) {
                logger.error("You cant take more than you have in the warehouse inventory.");
                throw new NotAcceptableException("You cant take more than you have in the warehouse inventory.");
            }
            inventory.setAmount(inventory.getAmount() + request.getAmount());

            //if products being taken away and less than 10 products left send warn log.
            if (inventory.getAmount() + request.getAmount() < 10 && request.getAmount() < 0) {
                logger.warn("You have less than ten products left in your inventory.");
            }
        } else {
            if (request.getAmount() < 0) {
                logger.error("You cant take products you dont have in the warehouse inventory.");
                throw new NotAcceptableException("You cant take products you dont have in the warehouse inventory.");
            }

            //gets data needed to build inventory object from database
            Product product = productRepository.getProductById(request.getProductId());
            Warehouse warehouse = warehouseRepository.getWarehouseById(request.getWarehouseId());
            if (product == null || warehouse == null) {
                logger.error("Product or warehouse with the given IDs not found.");
                throw new NotAcceptableException("Product or warehouse with the given IDs not found.");
            }
            inventory = Inventory.builder()
                    .product(product)
                    .warehouse(warehouse)
                    .amount(request.getAmount())
                    .build();
        }
        inventoryRepository.save(inventory);
        logger.info("Saved Product Inventory {} to Database", inventory);

        InventoryHistory inventoryHistory = InventoryHistory.builder()
                .productId(inventory.getProduct().getId())
                .amountChange(request.getAmount())
                .warehouseId(request.getWarehouseId())
                .build();
        kafkaEvent.batchInventoryHistory(inventoryHistory);
        kafkaEvent.deleteCacheByWarehouseName(request.getWarehouseId());

        logger.info("Saved Inventory History to Database");
    }

    public void deleteProduct(DeleteFromInventoryRequest request) {
        Inventory inventory = inventoryRepository.getInventoryByProduct_IdAndWarehouse_Id(request.getProductId(), request.getWarehouseId());
        if (inventory == null) {
            logger.error("Product not found.");
            throw new NotAcceptableException("Product not found.");
        }
        inventory.setDeleted(true);
        inventoryRepository.save(inventory);
        logger.info("Inventory soft deleted from database.");
    }

    public Set<InventoryDto> getByWarehouseName(String warehouseName) {
        if (warehouseName == null || warehouseName.trim().equals("")) {
            logger.error("Warehouse name cannot be empty.");
            throw new NotAcceptableException("Warehouse name cannot be empty.");
        }
        Set<Inventory> inventorySet = inventoryRepository.getInventoryByWarehouse_Name(warehouseName);

        if (inventorySet == null || inventorySet.size() == 0) {
            logger.error("Warehouse inventory by that name not found.");
            throw new NotAcceptableException("Warehouse inventory by that name not found.");
        }

        return mapInventoryDtos(inventorySet);
    }

    public Set<InventoryDto> getByCity(GetInvByCityRequest request) {
        if (request.getCity() == null || request.getCity().trim().equals("")) {
            logger.error("Warehouse city cannot be empty.");
            throw new NotAcceptableException("Warehouse city cannot be empty.");
        }

        Set<Inventory> inventorySet = inventoryRepository.getInventoryByWarehouse_City(request.getCity());

        if (inventorySet == null || inventorySet.size() == 0) {
            logger.error("Warehouse inventory in that city not found.");
            throw new NotAcceptableException("Warehouse inventory in that city not found.");
        }

        return mapInventoryDtos(inventorySet);
    }

    public Set<InventoryDto> getByRegion(GetInvByRegionRequest request) {
        if (request.getRegion() == null || request.getRegion().trim().equals("")) {
            logger.error("Warehouse region cannot be empty.");
            throw new NotAcceptableException("Warehouse region cannot be empty.");
        }

        Set<Inventory> inventorySet = inventoryRepository.getInventoryByWarehouse_Region(request.getRegion());

        if (inventorySet == null || inventorySet.size() == 0) {
            logger.error("Warehouse inventory in that region not found.");
            throw new NotAcceptableException("Warehouse inventory in that region not found.");
        }

        return mapInventoryDtos(inventorySet);
    }

    public Set<InventoryDto> getByProductCategory(GetInvByProductCategoryRequest request) {
        if (request.getCategory() == null || request.getCategory().trim().equals("")) {
            logger.error("Product category cannot be empty.");
            throw new NotAcceptableException("Product category cannot be empty.");
        }

        Set<Inventory> inventorySet = inventoryRepository.getInventoryByProduct_ProductCategory_Category(request.getCategory());

        if (inventorySet == null || inventorySet.size() == 0) {
            logger.error("Inventory with the given product category not found.");
            throw new NotAcceptableException("Inventory with the given product category not found.");
        }

        return mapInventoryDtos(inventorySet);
    }

    public Set<InventoryDto> getByProductId(GetInvByProductIdRequest request) {
        Set<Inventory> inventorySet = inventoryRepository.getInventoryByProduct_Id(request.getProductId());

        if (inventorySet == null || inventorySet.size() == 0) {
            logger.error("Inventory with the given product ID not found.");
            throw new NotAcceptableException("Inventory with the given product ID not found.");
        }

        return mapInventoryDtos(inventorySet);
    }

    public Set<InventoryDto> getByProductName(GetInvByProductNameRequest request) {
        if (request.getProductName() == null || request.getProductName().trim().equals("")) {
            logger.error("Product name cannot be empty.");
            throw new NotAcceptableException("Product name cannot be empty.");
        }

        Set<Inventory> inventorySet = inventoryRepository.getInventoryByProduct_Name(request.getProductName());

        if (inventorySet == null || inventorySet.size() == 0){
            logger.error("Inventory with the given product name not found.");
            throw new NotAcceptableException("Inventory with the given product name not found.");
        }

        return mapInventoryDtos(inventorySet);
    }

    public Set<InventoryDto> mapInventoryDtos(Set<Inventory> inventorySet) {
        Set<InventoryDto> inventoryDtoSet = new HashSet<>();
        /*for (Inventory inventory : inventorySet
        ) {
            inventoryDtoSet.add(InventoryDto.builder()
                    .inventoryId(inventory.getId())
                    .product(inventory.getProduct())
                    .warehouse(inventory.getWarehouse())
                    .amount(inventory.getAmount())
                    .isDeleted(inventory.isDeleted())
                    .updatedAt(inventory.getUpdatedAt())
                    .build());
        }*/
        inventorySet.forEach(inventory -> inventoryDtoSet.add(InventoryDto.builder()
                .inventoryId(inventory.getId())
                .product(inventory.getProduct())
                .warehouse(inventory.getWarehouse())
                .amount(inventory.getAmount())
                .isDeleted(inventory.isDeleted())
                .updatedAt(inventory.getUpdatedAt())
                .build()));
        return inventoryDtoSet;
    }

}
