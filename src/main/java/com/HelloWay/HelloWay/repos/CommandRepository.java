package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
    @Query("SELECT c FROM Command c WHERE c.basket.id_basket = :basketId")
    Command findCommandByBasketId(@Param("basketId") Long basketId);
}
