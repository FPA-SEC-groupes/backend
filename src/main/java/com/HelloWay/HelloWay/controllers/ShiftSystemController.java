package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.ShiftSystem;
import com.HelloWay.HelloWay.payload.request.ShiftSystemRequestDTO;
import com.HelloWay.HelloWay.payload.request.ShiftUpdateDTO;
import com.HelloWay.HelloWay.services.ShiftSystemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;


@RestController
@RequestMapping("/api/shiftsystems")
public class ShiftSystemController {

    @Autowired
    private ShiftSystemService shiftSystemService;

    
 
    @PostMapping
    public ShiftSystem createShift(@RequestBody ShiftSystemRequestDTO shiftSystemRequest) {
        return shiftSystemService.createShiftSystem(shiftSystemRequest);
    }


    @GetMapping("/{shiftId}")
    public Optional<ShiftSystem> getShiftById(@PathVariable Long shiftId) {
        return shiftSystemService.getShiftById(shiftId);
    }

    @GetMapping
    public List<ShiftSystem> getAllShifts() {
        return shiftSystemService.getAllShifts();
    }
    
    @GetMapping("/waiter/{waiterId}")
    public List<ShiftSystem> getShiftsByWaiterId(@PathVariable Long waiterId) {
        return shiftSystemService.getShiftsByWaiterId(waiterId);
    }

    @PutMapping("/{shiftId}")
    public ShiftSystem updateShift(@PathVariable Long shiftId, @RequestBody ShiftUpdateDTO shiftUpdateDTO) {
        return shiftSystemService.updateShift(shiftId, shiftUpdateDTO);
    }
    @DeleteMapping("/{shiftId}")
    public void deleteShift(@PathVariable Long shiftId) {
        shiftSystemService.deleteShift(shiftId);
    }
}