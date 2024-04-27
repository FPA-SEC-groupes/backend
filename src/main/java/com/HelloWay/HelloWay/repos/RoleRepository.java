package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.ERole;
import com.HelloWay.HelloWay.entities.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
