package com.bilgehan.envanter.service;

import com.bilgehan.envanter.kafka.producer.KafkaEvent;
import com.bilgehan.envanter.kafka.producer.KafkaProducer;
import com.bilgehan.envanter.model.dto.InventoryDto;
import com.bilgehan.envanter.model.entity.Inventory;
import com.bilgehan.envanter.model.entity.Product;
import com.bilgehan.envanter.model.entity.ProductCategory;
import com.bilgehan.envanter.model.entity.Warehouse;
import com.bilgehan.envanter.model.request.*;
import com.bilgehan.envanter.repository.InventoryHistoryRepository;
import com.bilgehan.envanter.repository.InventoryRepository;
import com.bilgehan.envanter.repository.ProductRepository;
import com.bilgehan.envanter.repository.WarehouseRepository;
import com.bilgehan.envanter.exception.NotAcceptableException;
import org.junit.Assert;
import org.junit.Before;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;


public class InventoryServiceTest {

    private InventoryService inventoryService;

    private InventoryRepository inventoryRepository;
    private InventoryHistoryRepository inventoryHistoryRepository;
    private ProductRepository productRepository;
    private WarehouseRepository warehouseRepository;
    private KafkaEvent kafkaEvent;


    @Before
    public void setUp() throws Exception {
        inventoryRepository = Mockito.mock(InventoryRepository.class);
        inventoryHistoryRepository = Mockito.mock(InventoryHistoryRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        warehouseRepository = Mockito.mock(WarehouseRepository.class);
        kafkaEvent = Mockito.mock(KafkaEvent.class);

        inventoryService = new InventoryService(
                inventoryRepository,
                inventoryHistoryRepository,
                productRepository,
                warehouseRepository,
                kafkaEvent);
    }

    @Test(expected = NotAcceptableException.class)
    public void whenAddProductCalledWithZeroAmount_itShouldThrowNotAcceptableException() {
        AddProductToInventoryRequest request = new AddProductToInventoryRequest();
        request.setAmount(0);
        request.setProductId(1);
        request.setWarehouseId(1);

        inventoryService.addProduct(request);
    }

    @Test(expected = NotAcceptableException.class)
    public void whenAddProductCalledWithMinusAmountToTakeFromInventoryButTheAmountIsLowerOnDatabase_ItShouldThrowNotAcceptableException() {
        AddProductToInventoryRequest request = new AddProductToInventoryRequest();
        request.setAmount(-5);
        request.setProductId(1);
        request.setWarehouseId(1);

        Inventory inventory = generateInventory();
        Mockito.when(inventoryRepository.existsByWarehouse_IdAndProduct_Id(request.getWarehouseId(), request.getProductId())).thenReturn(true);
        Mockito.when(inventoryRepository.getByWarehouse_IdAndProduct_Id(request.getWarehouseId(), request.getProductId())).thenReturn(inventory);

        inventoryService.addProduct(request);
    }

    @Test(expected = NotAcceptableException.class)
    public void whenAddProductCalledToMakeNewInventoryButTheAmountLessThanZero_ItShouldThrowNotAcceptableException() {
        AddProductToInventoryRequest request = new AddProductToInventoryRequest();
        request.setAmount(-5);
        request.setProductId(2);
        request.setWarehouseId(1);

        Inventory inventory = generateInventory();
        Product product = generateProduct();
        Warehouse warehouse = generateWarehouse();

        Mockito.when(inventoryRepository.existsByWarehouse_IdAndProduct_Id(request.getWarehouseId(), request.getProductId())).thenReturn(false);
        Mockito.when(inventoryRepository.getByWarehouse_IdAndProduct_Id(request.getWarehouseId(), request.getProductId())).thenReturn(inventory);
        Mockito.when(productRepository.getProductById(request.getProductId())).thenReturn(product);
        Mockito.when(warehouseRepository.getWarehouseById(request.getWarehouseId())).thenReturn(warehouse);

        inventoryService.addProduct(request);
    }

    @Test
    public void whenGetByWarehouseNameCalledWithValidRequest_ItShouldReturnValidInventoryDtoSet() {
        GetInvByWarehouseNameRequest request = new GetInvByWarehouseNameRequest();
        request.setWarehouseName("warehouse a");

        Set<Inventory> inventorySet = new HashSet<>();
        inventorySet.add(generateInventory());

        Mockito.when(inventoryRepository.getInventoryByWarehouse_Name(request.getWarehouseName())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByWarehouseName(request.getWarehouseName());

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByWarehouse_Name(request.getWarehouseName());
    }

    @Test(expected = NotAcceptableException.class)
    public void whenGetByWarehouseNameCalledWithNonExistingName_ItShouldThrowNotAcceptableException() {
        GetInvByWarehouseNameRequest request = new GetInvByWarehouseNameRequest();
        request.setWarehouseName("warehouse f");

        Set<Inventory> inventorySet = new HashSet<>();

        Mockito.when(inventoryRepository.getInventoryByWarehouse_Name(request.getWarehouseName())).thenReturn(inventorySet);
        Mockito.when(inventoryRepository.getInventoryByWarehouse_Name(request.getWarehouseName())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        //test should stop after this
        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByWarehouseName(request.getWarehouseName());

        Assert.assertNotEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByWarehouse_Name(request.getWarehouseName());
    }

    @Test
    public void whenGetByWarehouseCityCalledWithValidRequest_ItShouldReturnValidInventoryDtoSet() {
        GetInvByCityRequest request = new GetInvByCityRequest();
        request.setCity("antalya");

        Set<Inventory> inventorySet = new HashSet<>();
        inventorySet.add(generateInventory());

        Mockito.when(inventoryRepository.getInventoryByWarehouse_City(request.getCity())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByCity(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByWarehouse_City(request.getCity());
    }

    @Test(expected = NotAcceptableException.class)
    public void whenGetByWarehouseCityCalledWithNonExistingCity_ItShouldThrowNotAcceptableException() {
        GetInvByCityRequest request = new GetInvByCityRequest();
        request.setCity("");

        Set<Inventory> inventorySet = new HashSet<>();

        Mockito.when(inventoryRepository.getInventoryByWarehouse_City(request.getCity())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        //test should stop after this
        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByCity(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByWarehouse_City(request.getCity());
    }

    @Test
    public void whenGetByWarehouseRegionCalledWithValidRequest_ItShouldReturnValidInventoryDtoSet() {
        GetInvByRegionRequest request = new GetInvByRegionRequest();
        request.setRegion("akdeniz");

        Set<Inventory> inventorySet = new HashSet<>();
        inventorySet.add(generateInventory());

        Mockito.when(inventoryRepository.getInventoryByWarehouse_Region(request.getRegion())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByRegion(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByWarehouse_Region(request.getRegion());
    }

    @Test(expected = NotAcceptableException.class)
    public void whenGetByWarehouseRegionCalledWithNonExistingRegion_ItShouldThrowNotAcceptableException() {
        GetInvByRegionRequest request = new GetInvByRegionRequest();
        request.setRegion(" ");

        Set<Inventory> inventorySet = new HashSet<>();

        Mockito.when(inventoryRepository.getInventoryByWarehouse_Region(request.getRegion())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        //test should stop after this
        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByRegion(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByWarehouse_Region(request.getRegion());
    }

    @Test
    public void whenGetByProductCategoryCalledWithValidRequest_ItShouldReturnValidInventoryDtoSet() {
        GetInvByProductCategoryRequest request = new GetInvByProductCategoryRequest();
        request.setCategory("kitap");

        Set<Inventory> inventorySet = new HashSet<>();
        inventorySet.add(generateInventory());

        Mockito.when(inventoryRepository.getInventoryByProduct_ProductCategory_Category(request.getCategory())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByProductCategory(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByProduct_ProductCategory_Category(request.getCategory());
    }

    @Test(expected = NotAcceptableException.class)
    public void whenGetByProductCategoryCalledWithNonExistingCategory_ItShouldThrowNotAcceptableException() {
        GetInvByProductCategoryRequest request = new GetInvByProductCategoryRequest();
        request.setCategory(" ");

        Set<Inventory> inventorySet = new HashSet<>();

        Mockito.when(inventoryRepository.getInventoryByProduct_Name(request.getCategory())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        //test should stop after this
        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByProductCategory(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByProduct_Name(request.getCategory());
    }

    @Test
    public void whenGetByProductIdCalledWithValidRequest_ItShouldReturnValidInventoryDtoSet() {
        GetInvByProductIdRequest request = new GetInvByProductIdRequest();
        request.setProductId(1);

        Set<Inventory> inventorySet = new HashSet<>();
        inventorySet.add(generateInventory());

        Mockito.when(inventoryRepository.getInventoryByProduct_Id(request.getProductId())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByProductId(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByProduct_Id(request.getProductId());
    }

    @Test(expected = NotAcceptableException.class)
    public void whenGetByProductIdCalledWithNonExistingId_ItShouldThrowNotAcceptableException() {
        GetInvByProductIdRequest request = new GetInvByProductIdRequest();
        request.setProductId(100);

        Set<Inventory> inventorySet = new HashSet<>();

        Mockito.when(inventoryRepository.getInventoryByProduct_Id(request.getProductId())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        //test should stop after this
        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByProductId(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByProduct_Id(request.getProductId());
    }

    @Test
    public void whenGetByProductNameCalledWithValidRequest_ItShouldReturnValidInventoryDtoSet() {
        GetInvByProductNameRequest request = new GetInvByProductNameRequest();
        request.setProductName("Roman 1");

        Set<Inventory> inventorySet = new HashSet<>();
        inventorySet.add(generateInventory());

        Mockito.when(inventoryRepository.getInventoryByProduct_Name(request.getProductName())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByProductName(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByProduct_Name(request.getProductName());
    }

    @Test(expected = NotAcceptableException.class)
    public void whenGetByProductNameCalledWithNonExistingName_ItShouldThrowNotAcceptableException() {
        GetInvByProductNameRequest request = new GetInvByProductNameRequest();
        request.setProductName(" ");

        Set<Inventory> inventorySet = new HashSet<>();

        Mockito.when(inventoryRepository.getInventoryByProduct_Name(request.getProductName())).thenReturn(inventorySet);
        Set<InventoryDto> inventoryDtos = inventoryService.mapInventoryDtos(inventorySet);

        //test should stop after this
        Set<InventoryDto> inventoryDtoSetResult = inventoryService.getByProductName(request);

        Assert.assertEquals(inventoryDtoSetResult, inventoryDtos);

        Mockito.verify(inventoryRepository).getInventoryByProduct_Name(request.getProductName());
    }

    private Warehouse generateWarehouse() {
        return Warehouse.builder()
                .id(1)
                .name("warehouse a")
                .city("antalya")
                .region("akdeniz")
                .build();
    }

    private Product generateProduct() {
        ProductCategory productCategory = ProductCategory.builder()
                .id(3)
                .category("Kağıt")
                .build();
        return Product.builder()
                .id(2L)
                .name("A4 Kağıt")
                .productCategory(productCategory)
                .isDeleted(false)
                .build();
    }

    private Inventory generateInventory() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ProductCategory productCategory = ProductCategory.builder()
                .id(1)
                .category("Kitap")
                .build();
        Product product = Product.builder()
                .id(1L)
                .name("Roman 1")
                .productCategory(productCategory)
                .isDeleted(false)
                .build();

        Warehouse warehouse = generateWarehouse();

        return Inventory.builder()
                .id(1L)
                .amount(4)
                .product(product)
                .warehouse(warehouse)
                .isDeleted(false)
                .updatedAt(timestamp)
                .build();
    }
}