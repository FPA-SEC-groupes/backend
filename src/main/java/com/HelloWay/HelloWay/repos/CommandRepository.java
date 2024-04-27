package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
}
