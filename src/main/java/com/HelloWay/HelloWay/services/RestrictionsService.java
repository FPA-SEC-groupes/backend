package com.HelloWay.HelloWay.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HelloWay.HelloWay.entities.Restrictions;
import com.HelloWay.HelloWay.payload.request.RestrictionsDTO;
import com.HelloWay.HelloWay.repos.RestrictionsRepository;
import com.HelloWay.HelloWay.repos.UserRepository;

@Service
public class RestrictionsService {

    @Autowired
    private RestrictionsRepository restrictionsRepository;

    @Autowired
    private static UserRepository userRepo;

    public List<Restrictions> getAllRestrictions() {
        return restrictionsRepository.findAll();
    }

    public Optional<Restrictions> getRestrictionsById(Long id) {
        return restrictionsRepository.findById(id);
    }

    public Restrictions createRestrictions(RestrictionsDTO restrictionsDTO) {
        Restrictions restriction = restrictionsDTO.toEntity();
        return restrictionsRepository.save(restriction);
    }
    
    public Restrictions updateRestrictions(Long id, RestrictionsDTO restrictionsDTO) {
        Optional<Restrictions> existingRestrictions = restrictionsRepository.findById(id);
        if (existingRestrictions.isPresent()) {
            Restrictions restrictions = existingRestrictions.get();
            restrictions.setDescription(restrictionsDTO.getDescription());
            restrictions.setNumberOfRestrictions(restrictionsDTO.getNumberOfRestrictions());
            restrictions.setUser(userRepo.findById(restrictionsDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found")));
            return restrictionsRepository.save(restrictions);
        } else {
            throw new RuntimeException("Restrictions not found");
        }
    }
    

    public void deleteRestrictions(Long id) {
        restrictionsRepository.deleteById(id);
    }

    public int getNumberOfRestrictionsByUserId(Long userId) {
        return restrictionsRepository.countByUserId(userId);
    }
}
