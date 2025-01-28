package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);

    Optional<User> findByIdAndRolesContaining(Long id, String role);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.space.id = :spaceId")
    List<User> findByUsernameAndSpaceId(@Param("username") String username, @Param("spaceId") Long spaceId);











}
