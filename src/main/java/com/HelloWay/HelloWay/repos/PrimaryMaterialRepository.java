package com.HelloWay.HelloWay.repos;

import com.HelloWay.HelloWay.entities.PrimaryMaterial;
import com.HelloWay.HelloWay.entities.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrimaryMaterialRepository extends JpaRepository<PrimaryMaterial, Long> {
    List<PrimaryMaterial> findBySpace(Space space);
    Optional<PrimaryMaterial> findByIdAndSpace(Long primaryMaterialId, Space space);

    List<PrimaryMaterial> findBySpaceAndName(Space space, String name);

    List<PrimaryMaterial> findExpiredBySpace(Space space);

    List<PrimaryMaterial> findExpiredBySpaceAndExpirationDateBefore(Space space, LocalDateTime expirationDate);

}
