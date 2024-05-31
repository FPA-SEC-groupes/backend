package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Space;
import com.HelloWay.HelloWay.entities.Wifi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WifiRepository extends JpaRepository<Wifi, Long> {
    @Query("SELECT w FROM Wifi w WHERE w.space.id_space = ?1")
    List<Wifi> findWifisBySpaceId(Long idSpace);
    boolean existsBySsidAndSpace(String ssid, Space space);
}