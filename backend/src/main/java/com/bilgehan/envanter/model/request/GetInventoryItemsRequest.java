package com.bilgehan.envanter.model.request;

import com.bilgehan.envanter.model.FilterType;
import lombok.Data;

@Data
public class GetInventoryItemsRequest {
    private String filterValue;
    private FilterType filterType;
}
