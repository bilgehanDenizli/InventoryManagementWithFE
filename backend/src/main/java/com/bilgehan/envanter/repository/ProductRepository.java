package com.bilgehan.envanter.repository;

import com.bilgehan.envanter.model.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product getProductById(long productId);
    boolean existsProductByName(String name);

}
