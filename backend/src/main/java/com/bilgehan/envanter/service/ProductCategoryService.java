package com.bilgehan.envanter.service;

import com.bilgehan.envanter.model.dto.ProductCategoryDto;
import com.bilgehan.envanter.model.entity.ProductCategory;
import com.bilgehan.envanter.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<ProductCategoryDto> getCategories() {
        List<ProductCategory> categoryList = productCategoryRepository.findAll();
        List<ProductCategoryDto> categoryDtoList = new ArrayList<>();
        for (ProductCategory productCategory: categoryList
             ) {
            categoryDtoList.add(
                    ProductCategoryDto.builder()
                    .id(productCategory.getId())
                    .category(productCategory.getCategory())
                    .build()
            );
        }
        return categoryDtoList;
    }
}
