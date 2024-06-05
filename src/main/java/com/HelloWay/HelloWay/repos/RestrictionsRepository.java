package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Restrictions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RestrictionsRepository extends JpaRepository<Restrictions, Long> {

    @Query("SELECT COUNT(r) FROM Restrictions r WHERE r.user.id = :userId")
    int countByUserId(Long userId);
}