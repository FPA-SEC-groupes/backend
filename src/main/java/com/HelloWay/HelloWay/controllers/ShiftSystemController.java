package com.HelloWay.HelloWay.controllers;

import java.time.DayOfWeek;
import com.HelloWay.HelloWay.entities.ShiftSystem;
import com.HelloWay.HelloWay.payload.request.ShiftSystemRequestDTO;
import com.HelloWay.HelloWay.payload.request.ShiftTimeDTO;
import com.HelloWay.HelloWay.payload.request.ShiftUpdateDTO;
import com.HelloWay.HelloWay.payload.request.UpdateDayOffRequest;
import com.HelloWay.HelloWay.services.ShiftSystemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;


@RestController
@RequestMapping("/api/shiftsystems")
public class ShiftSystemController {

    private final ShiftSystemService shiftSystemService;

    public ShiftSystemController(ShiftSystemService shiftSystemService) {
        this.shiftSystemService = shiftSystemService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public List<ShiftSystem> createShift(@RequestBody ShiftSystemRequestDTO shiftSystemRequest) {
        return shiftSystemService.createShiftSystem(shiftSystemRequest);
    }

    @GetMapping("/{shiftId}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'WAITER')")
    public Optional<ShiftSystem> getShiftById(@PathVariable Long shiftId) {
        return shiftSystemService.getShiftById(shiftId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER')")
    public List<ShiftSystem> getAllShifts() {
        return shiftSystemService.getAllShifts();
    }
    
    @GetMapping("/waiter/{waiterId}")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER')")
    public List<ShiftSystem> getShiftsByWaiterId(@PathVariable Long waiterId) {
        return shiftSystemService.getShiftsByWaiterId(waiterId);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public List<ShiftSystem> updateShifts(@RequestBody List<ShiftTimeDTO> shiftUpdateDTOs) {
        return shiftSystemService.updateShiftsByDate(shiftUpdateDTOs);
    }
    @PutMapping("/updateDayOff")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public int updateDayOff(@RequestBody UpdateDayOffRequest request) {
        LocalDate start = LocalDate.parse(request.getStartDate());
        DayOfWeek newDay = DayOfWeek.valueOf(request.getNewDayOff().toUpperCase());
        return shiftSystemService.updateDayOff(request.getWaiterId(), newDay, start, request.getDurationInWeeks());
    }
    
    @DeleteMapping("/{shiftId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public void deleteShift(@PathVariable Long shiftId) {
        shiftSystemService.deleteShift(shiftId);
    }
}