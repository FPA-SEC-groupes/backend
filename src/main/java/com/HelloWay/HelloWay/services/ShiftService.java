package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.ERole;
import com.HelloWay.HelloWay.entities.Role;
import com.HelloWay.HelloWay.entities.Shift;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.repos.ShiftRepository;
import com.HelloWay.HelloWay.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private UserRepository userRepository;

   
    public Shift createShift(Long waiterId, LocalDate shiftDate, LocalTime startTime) {
        Optional<User> optionalUser = userRepository.findById(waiterId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
                Shift shift = new Shift();
                shift.setWaiter(user);
                shift.setShiftDate(shiftDate);
                shift.setStartTime(startTime);
                return shiftRepository.save(shift);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public Optional<Shift> getShiftById(Long shiftId) {
        return shiftRepository.findById(shiftId);
    }

    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    public List<Shift> getShiftsByUserId(Long userId) {
        return shiftRepository.findByWaiterId(userId);
    }

    public Shift updateShift(Long shiftId, LocalTime endTime) {
        Optional<Shift> optionalShift = shiftRepository.findById(shiftId);
        if (optionalShift.isPresent()) {
            Shift shift = optionalShift.get();
            shift.setEndTime(endTime);
            return shiftRepository.save(shift);
        } else {
            throw new IllegalArgumentException("Shift not found");
        }
    }

    public void deleteShift(Long shiftId) {
        shiftRepository.deleteById(shiftId);
    }
}
