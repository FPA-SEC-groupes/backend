package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
}
