package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Demande;
import com.HelloWay.HelloWay.repos.DemandeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DemandeService {

    @Autowired
    private DemandeRepository demandeRepository;

    public List<Demande> findAll() {
        return demandeRepository.findAll();
    }

    public Optional<Demande> findById(Long id) {
        return demandeRepository.findById(id);
    }

    public Demande save(Demande demande) {
        return demandeRepository.save(demande);
    }

    public void deleteById(Long id) {
        demandeRepository.deleteById(id);
    }

    // Add any additional business logic methods here
}