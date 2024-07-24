package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.PrimaryMaterial;
import com.HelloWay.HelloWay.entities.PrimaryMaterialHistory;
import com.HelloWay.HelloWay.entities.Space;
import com.HelloWay.HelloWay.repos.PrimaryMaterialHistoryRepository;
import com.HelloWay.HelloWay.repos.PrimaryMaterialRepository;
import com.HelloWay.HelloWay.repos.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrimaryMaterialService {

    private final PrimaryMaterialRepository primaryMaterialRepository;
    private final SpaceRepository  spaceRepository;
    private PrimaryMaterialHistoryRepository primaryMaterialHistoryRepository;

    @Autowired
    public PrimaryMaterialService(PrimaryMaterialRepository primaryMaterialRepository, SpaceRepository  spaceRepository, PrimaryMaterialHistoryRepository primaryMaterialHistoryRepository) {
        this.primaryMaterialRepository = primaryMaterialRepository;
        this.spaceRepository = spaceRepository;
        this.primaryMaterialHistoryRepository=primaryMaterialHistoryRepository;
    }

    public List<PrimaryMaterial> getAllPrimaryMaterials() {
        return primaryMaterialRepository.findAll();
    }

    public PrimaryMaterial getPrimaryMaterialById(Long id) {
        return primaryMaterialRepository.findById(id).orElse(null);
    }

    public PrimaryMaterial createPrimaryMaterial(PrimaryMaterial primaryMaterial) {
        return primaryMaterialRepository.save(primaryMaterial);
    }

    public PrimaryMaterial updatePrimaryMaterial(Long id, PrimaryMaterial updatedPrimaryMaterial) {
        PrimaryMaterial existingPrimaryMaterial = primaryMaterialRepository.findById(id).orElse(null);
        if (existingPrimaryMaterial != null) {
            existingPrimaryMaterial.setName(updatedPrimaryMaterial.getName());
            existingPrimaryMaterial.setDescription(updatedPrimaryMaterial.getDescription());
            existingPrimaryMaterial.setUnitOfMeasure(updatedPrimaryMaterial.getUnitOfMeasure());
            existingPrimaryMaterial.setStockQuantity(updatedPrimaryMaterial.getStockQuantity());
            existingPrimaryMaterial.setPrice(updatedPrimaryMaterial.getPrice());
            // existingPrimaryMaterial.setExpirationDate(updatedPrimaryMaterial.getExpirationDate());
            existingPrimaryMaterial.setSupplier(updatedPrimaryMaterial.getSupplier());
            existingPrimaryMaterial.setSupplierNumber(updatedPrimaryMaterial.getSupplierNumber());
            existingPrimaryMaterial.setSpace(updatedPrimaryMaterial.getSpace());
            double difference1 = existingPrimaryMaterial.getStockQuantity() - updatedPrimaryMaterial.getStockQuantity();
            if(existingPrimaryMaterial.getStockQuantity() != updatedPrimaryMaterial.getStockQuantity()){
                double difference = existingPrimaryMaterial.getStockQuantity() - updatedPrimaryMaterial.getStockQuantity();
        
                String state;
                if (difference < 0) {
                    state = "out";
                } else if (difference > 0) {
                    state = "entry";
                } else {
                    state = "no change"; // or any other appropriate state
                }
                PrimaryMaterialHistory materialHistory = new PrimaryMaterialHistory();
                materialHistory.setName(updatedPrimaryMaterial.getName());
                materialHistory.setDate(LocalDateTime.now());
                materialHistory.setStockQuantity(difference);
                materialHistory.setStatus(state);
                materialHistory.setPrimaryMaterial(existingPrimaryMaterial);
                primaryMaterialHistoryRepository.save(materialHistory);
            }
            
            return primaryMaterialRepository.save(existingPrimaryMaterial);
        }
        return null;
    }

    public void deletePrimaryMaterial(Long id) {
        primaryMaterialRepository.deleteById(id);
    }
    public List<PrimaryMaterial> getPrimaryMaterialsBySpace(Space space) {
        return primaryMaterialRepository.findBySpace(space);
    }

    public PrimaryMaterial addPrimaryMaterialToSpace(Long spaceId, PrimaryMaterial primaryMaterial) {
        // Check if the space exists
        Space space = spaceRepository.findById(spaceId).orElse(null);
        if (space != null) {
            // Set the space for the primary material
            primaryMaterial.setSpace(space);
            return primaryMaterialRepository.save(primaryMaterial);
        }
        return null;
    }

    public PrimaryMaterial updatePrimaryMaterialInSpace(Long spaceId, Long primaryMaterialId, PrimaryMaterial updatedPrimaryMaterial) {
        // Check if the space exists
        Space space = spaceRepository.findById(spaceId).orElse(null);
        if (space != null) {
            // Check if the primary material exists in the given space
            PrimaryMaterial existingPrimaryMaterial = primaryMaterialRepository.findByIdAndSpace(primaryMaterialId, space).orElse(null);
            if (existingPrimaryMaterial != null) {
                double existingQuantity = existingPrimaryMaterial.getStockQuantity();
                double updatedQuantity = updatedPrimaryMaterial.getStockQuantity();
                double difference = updatedQuantity - existingQuantity;
                if (existingQuantity != updatedQuantity) { 
                    String state;
                    if (difference < 0) {
                        state = "out";
                    } else if (difference > 0) {
                        state = "entry";
                    } else {
                        state = "no change"; // or any other appropriate state
                    }
                    PrimaryMaterialHistory materialHistory = new PrimaryMaterialHistory();
                    materialHistory.setName(updatedPrimaryMaterial.getName());
                    materialHistory.setDate(LocalDateTime.now());
                    materialHistory.setStockQuantity(difference);
                    materialHistory.setStatus(state);
                    materialHistory.setPrimaryMaterial(existingPrimaryMaterial);
                    primaryMaterialHistoryRepository.save(materialHistory);
                }
                // Update the primary material attributes
                existingPrimaryMaterial.setName(updatedPrimaryMaterial.getName());
                existingPrimaryMaterial.setDescription(updatedPrimaryMaterial.getDescription());
                existingPrimaryMaterial.setUnitOfMeasure(updatedPrimaryMaterial.getUnitOfMeasure());
                existingPrimaryMaterial.setStockQuantity(updatedPrimaryMaterial.getStockQuantity());
                existingPrimaryMaterial.setPrice(updatedPrimaryMaterial.getPrice());
                existingPrimaryMaterial.setSupplier(updatedPrimaryMaterial.getSupplier());
                existingPrimaryMaterial.setSupplierNumber(updatedPrimaryMaterial.getSupplierNumber());
                return primaryMaterialRepository.save(existingPrimaryMaterial);
            }
        }
        return null;
    }
             public void removePrimaryMaterialFromSpace(Long spaceId, Long primaryMaterialId) {
             // Check if the space exists
             Space space = spaceRepository.findById(spaceId).orElse(null);
             if (space != null) {
                 // Check if the primary material exists in the given space
                 PrimaryMaterial primaryMaterial = primaryMaterialRepository.findByIdAndSpace(primaryMaterialId, space).orElse(null);
                 if (primaryMaterial != null) {
                     primaryMaterialRepository.delete(primaryMaterial);
                 }
             }
         }

        public List<PrimaryMaterial> getPrimaryMaterialsBySpaceAndName(Space space, String name) {
            return primaryMaterialRepository.findBySpaceAndName(space, name);
        }

        public List<PrimaryMaterial> getExpiredPrimaryMaterialsBySpace(Space space) {
            return primaryMaterialRepository.findExpiredBySpace(space);
        }

    // public List<PrimaryMaterial> getExpiredPrimaryMaterialsBySpaceLatestVersion(Space space) {
    //     LocalDateTime dateTime = LocalDateTime.now();
    //     return primaryMaterialRepository.findExpiredBySpaceAndExpirationDateBefore(space, dateTime);
    // }

    public PrimaryMaterial updatePrimaryMaterialQuantity(Long primaryMaterialId, double quantity) {
        PrimaryMaterial primaryMaterial = getPrimaryMaterialById(primaryMaterialId);
        primaryMaterial.setStockQuantity(quantity);
        return primaryMaterialRepository.save(primaryMaterial);
    }
}
