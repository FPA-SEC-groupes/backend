package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
}
