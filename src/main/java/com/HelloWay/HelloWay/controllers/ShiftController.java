package com.HelloWay.HelloWay.controllers;


import com.HelloWay.HelloWay.entities.Shift;
import com.HelloWay.HelloWay.payload.request.ShiftRequest;
import com.HelloWay.HelloWay.services.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping
    @PreAuthorize("hasAnyRole('WAITER')")
    public Shift createShift(@RequestBody ShiftRequest shiftRequest) {
        LocalDate shiftDate = shiftRequest.getShiftDate();
        LocalTime startTime = shiftRequest.getStartTime();
        LocalTime endTime = shiftRequest.getEndTime();
        return shiftService.createShift(shiftRequest.getWaiterId(), shiftDate, startTime, endTime);
    }

    @GetMapping("/{shiftId}")
    public Optional<Shift> getShiftById(@PathVariable Long shiftId) {
        return shiftService.getShiftById(shiftId);
    }

    @GetMapping
    public List<Shift> getAllShifts() {
        return shiftService.getAllShifts();
    }

    @GetMapping("/user/{userId}")
    public List<Shift> getShiftsByUserId(@PathVariable Long userId) {
        return shiftService.getShiftsByUserId(userId);
    }

    @PutMapping("/{shiftId}")
    public Shift updateShift(@PathVariable Long shiftId, @RequestParam String date, @RequestParam String start, @RequestParam String end) {
        LocalDate shiftDate = LocalDate.parse(date);
        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        return shiftService.updateShift(shiftId, shiftDate, startTime, endTime);
    }

    @DeleteMapping("/{shiftId}")
    public void deleteShift(@PathVariable Long shiftId) {
        shiftService.deleteShift(shiftId);
    }
}
