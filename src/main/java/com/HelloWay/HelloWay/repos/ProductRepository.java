package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Boolean existsByProductTitle(String productTitle);
}
