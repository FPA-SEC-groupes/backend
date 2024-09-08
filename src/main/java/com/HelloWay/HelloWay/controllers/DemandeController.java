package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.Demande;
import com.HelloWay.HelloWay.services.DemandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/demandes")
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Demande>> getAllDemandes() {
        List<Demande> demandes = demandeService.findAll();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Demande> getDemandeById(@PathVariable Long id) {
        Optional<Demande> demande = demandeService.findById(id);
        return demande.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    

    @PutMapping("/{id}")
    public ResponseEntity<Demande> updateDemande(@PathVariable Long id, @RequestBody Demande demandeDetails) {
        Optional<Demande> existingDemande = demandeService.findById(id);
        if (existingDemande.isPresent()) {
            Demande demande = existingDemande.get();
            // Update the fields of the existing Demande
            demande.setNom(demandeDetails.getNom());
            demande.setPrenom(demandeDetails.getPrenom());
            demande.setPhone(demandeDetails.getPhone());
            demande.setEmail(demandeDetails.getEmail());
            demande.setSubject(demandeDetails.getSubject());
            demande.setRestaurant(demandeDetails.getRestaurant());
            demande.setLocation(demandeDetails.getLocation());
            demande.setDescription(demandeDetails.getDescription());
            // Save the updated Demande
            Demande updatedDemande = demandeService.save(demande);
            return ResponseEntity.ok(updatedDemande);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemande(@PathVariable Long id) {
        demandeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}