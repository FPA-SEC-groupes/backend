package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.PrimaryMaterialHistory;
import com.HelloWay.HelloWay.repos.PrimaryMaterialHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrimaryMaterialHistoryService {

    @Autowired
    private PrimaryMaterialHistoryRepository primaryMaterialHistoryRepository;

    public List<PrimaryMaterialHistory> getAllPrimaryMaterialHistories() {
        return primaryMaterialHistoryRepository.findAll();
    }

    public Optional<PrimaryMaterialHistory> getPrimaryMaterialHistoryById(Long id) {
        return primaryMaterialHistoryRepository.findById(id);
    }

    public List<PrimaryMaterialHistory> getPrimaryMaterialHistoriesByPrimaryMaterialId(Long primaryMaterialId) {
        return primaryMaterialHistoryRepository.findByPrimaryMaterialId(primaryMaterialId);
    }

    public PrimaryMaterialHistory savePrimaryMaterialHistory(PrimaryMaterialHistory primaryMaterialHistory) {
        return primaryMaterialHistoryRepository.save(primaryMaterialHistory);
    }

    public void deletePrimaryMaterialHistory(Long id) {
        primaryMaterialHistoryRepository.deleteById(id);
    }
}
