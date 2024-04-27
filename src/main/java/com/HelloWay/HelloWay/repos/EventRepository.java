package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Event;
import com.HelloWay.HelloWay.entities.Party;
import com.HelloWay.HelloWay.entities.Promotion;
import com.HelloWay.HelloWay.entities.Space;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE TYPE(e) = Promotion")
    List<Promotion> findAllPromotions();

    @Query("SELECT e FROM Event e WHERE TYPE(e) = Party")
    List<Party> findAllParties();


    List<Event> findBySpace(Space space);

    List<Event> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM Event e WHERE e.startDate > :currentDate ORDER BY e.startDate ASC")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDate currentDate, Pageable pageable);

}
