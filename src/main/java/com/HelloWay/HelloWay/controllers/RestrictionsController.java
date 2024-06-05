package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.Restrictions;
import com.HelloWay.HelloWay.payload.request.RestrictionsDTO;
import com.HelloWay.HelloWay.services.RestrictionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restrictions")
public class RestrictionsController {

    @Autowired
    private RestrictionsService restrictionsService;

    @GetMapping
    public ResponseEntity<List<Restrictions>> getAllRestrictions() {
        List<Restrictions> restrictions = restrictionsService.getAllRestrictions();
        return new ResponseEntity<>(restrictions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restrictions> getRestrictionsById(@PathVariable Long id) {
        Optional<Restrictions> restrictions = restrictionsService.getRestrictionsById(id);
        return restrictions.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Restrictions> createRestrictions(@RequestBody RestrictionsDTO restrictions) {
        Restrictions createdRestrictions = restrictionsService.createRestrictions(restrictions);
        return new ResponseEntity<>(createdRestrictions, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restrictions> updateRestrictions(@PathVariable Long id, @RequestBody RestrictionsDTO restrictionsDTO) {
        Restrictions updatedRestrictions = restrictionsService.updateRestrictions(id, restrictionsDTO);
        return new ResponseEntity<>(updatedRestrictions, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestrictions(@PathVariable Long id) {
        restrictionsService.deleteRestrictions(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Integer> getNumberOfRestrictionsByUserId(@PathVariable Long userId) {
        int numberOfRestrictions = restrictionsService.getNumberOfRestrictionsByUserId(userId);
        return new ResponseEntity<>(numberOfRestrictions, HttpStatus.OK);
    }
}
