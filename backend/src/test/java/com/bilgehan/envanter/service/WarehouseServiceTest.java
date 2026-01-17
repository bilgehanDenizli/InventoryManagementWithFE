package com.bilgehan.envanter.service;

import com.bilgehan.envanter.model.dto.WarehouseDto;
import com.bilgehan.envanter.model.entity.Warehouse;
import com.bilgehan.envanter.model.request.GetWarehousesRequest;
import com.bilgehan.envanter.repository.WarehouseRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;


public class WarehouseServiceTest {

    private WarehouseService warehouseService;

    private WarehouseRepository warehouseRepository;

    @Before
    public void setUp() throws Exception {
        warehouseRepository = Mockito.mock(WarehouseRepository.class);

        warehouseService = new WarehouseService(warehouseRepository);
    }

    @Test
    public void whenGetWarehousesCalled_itShouldReturnMaxOfGivenLimitWarehouses() {
        GetWarehousesRequest request = new GetWarehousesRequest();
        request.setPage(0);
        request.setLimit(10);

        List<Warehouse> warehouseList = generateWarehouseList();
        Page<Warehouse> warehousePage = new PageImpl<>(warehouseList);
        List<WarehouseDto> warehouseDtoList = generateWarehouseDtoList();

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit());
        Mockito.when(warehouseRepository.findAllWithPagingOrderById(pageRequest)).thenReturn(warehousePage);

        List<WarehouseDto> warehousePageExpectedResult = warehouseService.getWarehouses(request);

        Assert.assertEquals(warehousePageExpectedResult,warehouseDtoList);
        Mockito.verify(warehouseRepository).findAllWithPagingOrderById(pageRequest);

    }

    private List<WarehouseDto> generateWarehouseDtoList() {
        List<WarehouseDto> warehouseDtoList = new ArrayList<>();
        warehouseDtoList.add(WarehouseDto.builder()
                .id(1)
                .name("warehouse a")
                .city("antalya")
                .region("akdeniz")
                .build());
        warehouseDtoList.add(WarehouseDto.builder()
                .id(2)
                .name("warehouse b")
                .city("zonguldak")
                .region("karadeniz")
                .build());

        return warehouseDtoList;
    }

    private List<Warehouse> generateWarehouseList() {
        List<Warehouse> warehouseList = new ArrayList<>();
        warehouseList.add(Warehouse.builder()
                .id(1)
                .name("warehouse a")
                .city("antalya")
                .region("akdeniz")
                .build());
        warehouseList.add(Warehouse.builder()
                .id(2)
                .name("warehouse b")
                .city("zonguldak")
                .region("karadeniz")
                .build());

        return warehouseList;
    }

}