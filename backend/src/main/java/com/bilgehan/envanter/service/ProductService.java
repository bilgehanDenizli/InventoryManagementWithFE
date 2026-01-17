package com.bilgehan.envanter.service;

import com.bilgehan.envanter.model.dto.ProductDto;
import com.bilgehan.envanter.model.entity.Product;
import com.bilgehan.envanter.model.request.UpdateProductRequest;
import com.bilgehan.envanter.repository.ProductRepository;
import com.bilgehan.envanter.model.request.AddProductRequest;
import com.bilgehan.envanter.exception.NotAcceptableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;

    }

    public ProductDto getProductById(long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            logger.error("Product not found.");
            throw new NotAcceptableException("Product not found.");
        }

        return ProductDto.builder()
                .id(product.get().getId())
                .name(product.get().getName())
                .productCategory(product.get().getProductCategory())
                .build();
    }

    public List<ProductDto> getProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductDto> productDtoList = new ArrayList<>();
        for (Product product : productList
        ) {
            productDtoList.add(
                    ProductDto.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .productCategory(product.getProductCategory())
                            .build()
            );
        }
        return productDtoList;
    }

    public void updateProduct(UpdateProductRequest request) {
        Product product = productRepository.getProductById(request.getId());
        //null and empty checks for when user does not want to change those fields
        if (request.getName() != null && !request.getName().trim().equals(""))
            product.setName(request.getName());

        //needs only ID, not the category name to update
        if (request.getProductCategory().getId() > 0) {
            product.setProductCategory(request.getProductCategory());
        }

        if (request.isDeleted())
            product.setDeleted(request.isDeleted());

        productRepository.save(product);
        logger.info("Product with ID: " + request.getId() + " is updated.");
    }

    public void addProduct(AddProductRequest request) {
        checkProduct(request.getName());

        Product product = Product.builder()
                .name(request.getName())
                .productCategory(request.getProductCategory())
                .build();
        productRepository.save(product);
        logger.info("Product with ID: " + product.getId() + " is inserted.");
    }

    private void checkProduct(String name) {
        boolean flag = productRepository.existsProductByName(name);
        if (flag) {
            logger.error("Product with this name already exists.");
            throw new NotAcceptableException("Product with this name already exists.");
        }
    }
}
