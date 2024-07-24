package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.PrimaryMaterialHistory;
import com.HelloWay.HelloWay.services.PrimaryMaterialHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/primaryMaterialHistories")
public class PrimaryMaterialHistoryController {

    @Autowired
    private PrimaryMaterialHistoryService primaryMaterialHistoryService;

    @GetMapping
    public List<PrimaryMaterialHistory> getAllPrimaryMaterialHistories() {
        return primaryMaterialHistoryService.getAllPrimaryMaterialHistories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrimaryMaterialHistory> getPrimaryMaterialHistoryById(@PathVariable Long id) {
        Optional<PrimaryMaterialHistory> primaryMaterialHistory = primaryMaterialHistoryService.getPrimaryMaterialHistoryById(id);
        return primaryMaterialHistory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/byPrimaryMaterial/{primaryMaterialId}")
    public List<PrimaryMaterialHistory> getPrimaryMaterialHistoriesByPrimaryMaterialId(@PathVariable Long primaryMaterialId) {
        return primaryMaterialHistoryService.getPrimaryMaterialHistoriesByPrimaryMaterialId(primaryMaterialId);
    }

    @PostMapping
    public PrimaryMaterialHistory createPrimaryMaterialHistory(@RequestBody PrimaryMaterialHistory primaryMaterialHistory) {
        return primaryMaterialHistoryService.savePrimaryMaterialHistory(primaryMaterialHistory);
    }

    @PutMapping("/{id}")
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
