package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Reclamation;
import com.HelloWay.HelloWay.repos.ReclamationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReclamationService {
    @Autowired
    ReclamationRepository reclamationRepository ;
    public List<Reclamation> findAllReclamations() {
        return reclamationRepository.findAll();
    }

    public Reclamation updateReclamation(Reclamation reservation) {
        return reclamationRepository.save(reservation);
    }

    public Reclamation findReclamationById(Long id) {
        return reclamationRepository.findById(id)
                .orElse(null);
    }

    public void deleteReclamation(Long id) {
        reclamationRepository.deleteById(id);
    }
}
