package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.PrimaryMaterialHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrimaryMaterialHistoryRepository extends JpaRepository<PrimaryMaterialHistory, Long> {
    List<PrimaryMaterialHistory> findByPrimaryMaterialId(Long primaryMaterialId);
}
