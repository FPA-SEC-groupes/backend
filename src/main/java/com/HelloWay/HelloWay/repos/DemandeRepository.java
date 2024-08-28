package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
    // You can add custom query methods here if needed
}