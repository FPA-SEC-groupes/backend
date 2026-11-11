package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.PrimaryMaterialHistory;
import com.HelloWay.HelloWay.services.PrimaryMaterialHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/primaryMaterialHistories")
public class PrimaryMaterialHistoryController {

    @Autowired
    private PrimaryMaterialHistoryService primaryMaterialHistoryService;

    @GetMapping

    @PreAuthorize("hasAnyRole('ADMIN')")    public List<PrimaryMaterialHistory> getAllPrimaryMaterialHistories() {
        return primaryMaterialHistoryService.getAllPrimaryMaterialHistories();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<PrimaryMaterialHistory> getPrimaryMaterialHistoryById(@PathVariable Long id) {
        Optional<PrimaryMaterialHistory> primaryMaterialHistory = primaryMaterialHistoryService.getPrimaryMaterialHistoryById(id);
        return primaryMaterialHistory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/byPrimaryMaterial/{primaryMaterialId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public List<PrimaryMaterialHistory> getPrimaryMaterialHistoriesByPrimaryMaterialId(@PathVariable Long primaryMaterialId) {
        return primaryMaterialHistoryService.getPrimaryMaterialHistoriesByPrimaryMaterialId(primaryMaterialId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public PrimaryMaterialHistory createPrimaryMaterialHistory(@RequestBody PrimaryMaterialHistory primaryMaterialHistory) {
        return primaryMaterialHistoryService.savePrimaryMaterialHistory(primaryMaterialHistory);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<PrimaryMaterialHistory> updatePrimaryMaterialHistory(@PathVariable Long id, @RequestBody PrimaryMaterialHistory primaryMaterialHistory) {
        Optional<PrimaryMaterialHistory> existingPrimaryMaterialHistory = primaryMaterialHistoryService.getPrimaryMaterialHistoryById(id);
        if (existingPrimaryMaterialHistory.isPresent()) {
            primaryMaterialHistory.setId(id);
            return ResponseEntity.ok(primaryMaterialHistoryService.savePrimaryMaterialHistory(primaryMaterialHistory));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<Void> deletePrimaryMaterialHistory(@PathVariable Long id) {
        Optional<PrimaryMaterialHistory> primaryMaterialHistory = primaryMaterialHistoryService.getPrimaryMaterialHistoryById(id);
        if (primaryMaterialHistory.isPresent()) {
            primaryMaterialHistoryService.deletePrimaryMaterialHistory(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
