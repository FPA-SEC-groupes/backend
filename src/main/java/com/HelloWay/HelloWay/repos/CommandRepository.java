package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
    boolean existsBySessionId(String sessionId);
    @Query("SELECT c FROM Command c WHERE c.basket.id_basket = :basketId")
    Command findCommandByBasketId(@Param("basketId") Long basketId);
    @Query("SELECT c FROM Command c WHERE c.space.id = :spaceId")
    List<Command> findCommandBySpaceId(@Param("spaceId") Long spaceId);
    @Query("SELECT c FROM Command c WHERE c.sessionId = :sessionId")
    Command findBySessionId(@Param("sessionId") String sessionId);

}   
