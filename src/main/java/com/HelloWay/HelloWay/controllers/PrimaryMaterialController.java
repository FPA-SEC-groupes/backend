package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.PrimaryMaterial;
import com.HelloWay.HelloWay.entities.Space;
import com.HelloWay.HelloWay.services.PrimaryMaterialService;
import com.HelloWay.HelloWay.services.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/primary-materials")
public class PrimaryMaterialController {

    private final PrimaryMaterialService primaryMaterialService;
    private final SpaceService spaceService;

    @Autowired
    public PrimaryMaterialController(PrimaryMaterialService primaryMaterialService, SpaceService spaceService) {
        this.primaryMaterialService = primaryMaterialService;
        this.spaceService = spaceService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<List<PrimaryMaterial>> getAllPrimaryMaterials() {
        List<PrimaryMaterial> primaryMaterials = primaryMaterialService.getAllPrimaryMaterials();
        return ResponseEntity.ok(primaryMaterials);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<PrimaryMaterial> getPrimaryMaterialById(@PathVariable("id") Long id) {
        PrimaryMaterial primaryMaterial = primaryMaterialService.getPrimaryMaterialById(id);
        if (primaryMaterial != null) {
            return ResponseEntity.ok(primaryMaterial);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<PrimaryMaterial> createPrimaryMaterial(@RequestBody PrimaryMaterial primaryMaterial) {
        PrimaryMaterial createdPrimaryMaterial = primaryMaterialService.createPrimaryMaterial(primaryMaterial);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPrimaryMaterial);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<PrimaryMaterial> updatePrimaryMaterial(
            @PathVariable("id") Long id,
            @RequestBody PrimaryMaterial updatedPrimaryMaterial
    ) {
        PrimaryMaterial updatedMaterial = primaryMaterialService.updatePrimaryMaterial(id, updatedPrimaryMaterial);
        if (updatedMaterial != null) {
            return ResponseEntity.ok(updatedMaterial);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<Void> deletePrimaryMaterial(@PathVariable("id") Long id) {
        primaryMaterialService.deletePrimaryMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/space/{spaceId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<List<PrimaryMaterial>> getPrimaryMaterialsBySpaceId(@PathVariable("spaceId") Long spaceId) {
        Space space = spaceService.findSpaceById(spaceId);
        List<PrimaryMaterial> primaryMaterials = primaryMaterialService.getPrimaryMaterialsBySpace(space);
        return ResponseEntity.ok(primaryMaterials);
    }

    @PostMapping("/space/{spaceId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<PrimaryMaterial> addPrimaryMaterialToSpace(
            @PathVariable("spaceId") Long spaceId,
            @RequestBody PrimaryMaterial primaryMaterial
    ) {
        PrimaryMaterial createdPrimaryMaterial = primaryMaterialService.addPrimaryMaterialToSpace(spaceId, primaryMaterial);
        if (createdPrimaryMaterial != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPrimaryMaterial);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/space/{spaceId}/{primaryMaterialId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<PrimaryMaterial> updatePrimaryMaterialInSpace(
            @PathVariable("spaceId") Long spaceId,
            @PathVariable("primaryMaterialId") Long primaryMaterialId,
            @RequestBody PrimaryMaterial updatedPrimaryMaterial
    ) {
        PrimaryMaterial updatedPrimaryMaterialLatest = primaryMaterialService.updatePrimaryMaterialInSpace(spaceId, primaryMaterialId, updatedPrimaryMaterial);
        if (updatedPrimaryMaterial != null) {
            return ResponseEntity.ok(updatedPrimaryMaterialLatest);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/space/{spaceId}/{primaryMaterialId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<Void> removePrimaryMaterialFromSpace(
            @PathVariable("spaceId") Long spaceId,
            @PathVariable("primaryMaterialId") Long primaryMaterialId
    ) {
        primaryMaterialService.removePrimaryMaterialFromSpace(spaceId, primaryMaterialId);
        return ResponseEntity.noContent().build();
    }

   @GetMapping("/space/{spaceId}/expiration")
   @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<List<PrimaryMaterial>> getExpiredPrimaryMaterialsBySpaceId(@PathVariable("spaceId") Long spaceId) {
        Space space = spaceService.findSpaceById(spaceId);
        List<PrimaryMaterial> expiredPrimaryMaterials = primaryMaterialService.getExpiredPrimaryMaterialsBySpace(space);
        return ResponseEntity.ok(expiredPrimaryMaterials);
    }

    @GetMapping("/space/{spaceId}/expiration/expired")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<List<PrimaryMaterial>> getExpiredPrimaryMaterialsBySpaceIdLatestVersion(@PathVariable("spaceId") Long spaceId) {
        Space space = spaceService.findSpaceById(spaceId);
        List<PrimaryMaterial> expiredPrimaryMaterials = primaryMaterialService.getExpiredPrimaryMaterialsBySpaceLatestVersion(space);
        return ResponseEntity.ok(expiredPrimaryMaterials);
    }

    @PatchMapping("/{primaryMaterialId}/quantity")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<PrimaryMaterial> updatePrimaryMaterialQuantity(
            @PathVariable("primaryMaterialId") Long primaryMaterialId,
            @RequestParam("quantity") double quantity
    ) {
        PrimaryMaterial updatedPrimaryMaterial = primaryMaterialService.updatePrimaryMaterialQuantity(primaryMaterialId, quantity);
        if (updatedPrimaryMaterial != null) {
            return ResponseEntity.ok(updatedPrimaryMaterial);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/space/{spaceId}/name/{name}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<List<PrimaryMaterial>> getPrimaryMaterialsBySpaceIdAndName(
            @PathVariable("spaceId") Long spaceId,
            @PathVariable("name") String name
    ) {
        Space space = spaceService.findSpaceById(spaceId);
        List<PrimaryMaterial> primaryMaterials = primaryMaterialService.getPrimaryMaterialsBySpaceAndName(space, name);
        return ResponseEntity.ok(primaryMaterials);
    }
}
