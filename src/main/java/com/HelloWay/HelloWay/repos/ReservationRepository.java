package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByStartDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<Reservation> findByStartDateAfterOrderByStartDateAsc(LocalDateTime startDateTime);
}
