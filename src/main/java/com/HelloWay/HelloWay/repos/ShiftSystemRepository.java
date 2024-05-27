package com.HelloWay.HelloWay.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.HelloWay.HelloWay.entities.ShiftSystem;

public interface ShiftSystemRepository extends JpaRepository<ShiftSystem, Long> {
    
    @Query("SELECT s FROM ShiftSystem s WHERE s.waiter.id = :waiterId")
    List<ShiftSystem> findByWaiterId(Long waiterId);
}
