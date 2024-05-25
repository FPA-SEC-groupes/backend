package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Shift;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByWaiterId(Long waiterId);
}
