package com.HelloWay.HelloWay.repos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.HelloWay.HelloWay.entities.ShiftSystem;
import com.HelloWay.HelloWay.entities.User;

public interface ShiftSystemRepository extends JpaRepository<ShiftSystem, Long> {
    
    @Query("SELECT s FROM ShiftSystem s WHERE s.waiter.id = :waiterId")
    List<ShiftSystem> findByWaiterId(Long waiterId);
    Optional<ShiftSystem> findByDate(LocalDate date);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ShiftSystem s WHERE s.waiter.id = :waiterId AND s.date IN :date")
    void deleteByWaiterIdAndDate(Long waiterId, LocalDate date);
    Optional<ShiftSystem> findByWaiterAndDateAndStartTimeAndEndTime(User waiter, LocalDate date, LocalTime startTime, LocalTime endTime);
}
