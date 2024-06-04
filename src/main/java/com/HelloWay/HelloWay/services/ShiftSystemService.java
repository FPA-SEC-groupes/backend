package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Shift;
import com.HelloWay.HelloWay.entities.ShiftSystem;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.payload.request.ShiftSystemRequestDTO;
import com.HelloWay.HelloWay.payload.request.ShiftTimeDTO;
import com.HelloWay.HelloWay.payload.request.ShiftUpdateDTO;
import com.HelloWay.HelloWay.repos.ShiftSystemRepository;
import com.HelloWay.HelloWay.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class ShiftSystemService {

    private final ShiftSystemRepository shiftSystemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShiftSystemService(ShiftSystemRepository shiftSystemRepository, UserRepository userRepository) {
        this.shiftSystemRepository = shiftSystemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ShiftSystem createShiftSystem(ShiftSystemRequestDTO shiftSystemRequestDTO) {
        Optional<User> user = userRepository.findById(shiftSystemRequestDTO.getWaiterId());
        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }
        
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(shiftSystemRequestDTO.getDayOfWeek().toUpperCase());

        User waiter = user.get();
        ShiftSystem shiftSystem = new ShiftSystem();
        shiftSystem.setWaiter(waiter);
        shiftSystem.setDayOfWeek(dayOfWeek);
        shiftSystem.setDate(shiftSystemRequestDTO.getDate());
        shiftSystem.setStartTime(shiftSystemRequestDTO.getStartTime());
        shiftSystem.setEndTime(shiftSystemRequestDTO.getEndTime());

        return shiftSystemRepository.save(shiftSystem); // Save the shiftSystem to the database
    }
    
    // public List<ShiftSystem> createShift(ShiftSystemRequestDTO shiftSystemRequest) {
    //     List<ShiftSystem> shiftSystems = new ArrayList<>();
    //     Optional<User> userOptional = userRepository.findById(shiftSystemRequest.getWaiterId());
        
    //     if (userOptional.isPresent()) {
    //         User waiter = userOptional.get();
            
    //         for (ShiftTimeDTO shiftTimeDTO : shiftSystemRequest.getShiftTimes()) {
    //             DayOfWeek dayOfWeek = DayOfWeek.valueOf(shiftTimeDTO.getDayOfWeek().toUpperCase());
    //             LocalTime startTime = LocalTime.parse(shiftTimeDTO.getStartTime());
    //             LocalTime endTime = LocalTime.parse(shiftTimeDTO.getEndTime());
    //             LocalDate shiftDate = shiftTimeDTO.getShiftDate();

    //             ShiftSystem shiftSystem = new ShiftSystem();
    //             shiftSystem.setWaiter(waiter);
    //             shiftSystem.setDayOfWeek(dayOfWeek);
    //             shiftSystem.setStartTime(startTime);
    //             shiftSystem.setEndTime(endTime);
    //             shiftSystem.setDate(shiftDate);
                
    //             shiftSystems.add(shiftSystem);
    //         }
    //     }
        
    //     return shiftSystemRepository.saveAll(shiftSystems);
    // }
    
    // public ShiftSystem createShiftSystem(ShiftSystemRequestDTO shiftSystemRequestDTO) {
    //     Optional<User> user = userRepository.findById(shiftSystemRequestDTO.getWaiterId());
    //     DayOfWeek dayOfWeek = DayOfWeek.valueOf(shiftSystemRequestDTO.getDayOfWeek().toUpperCase());
    //     if (!user.isPresent()) {
    //         throw new RuntimeException("User not found");
    //     }
    //     User waiter = user.get();
    //     ShiftSystem shiftSystem = new ShiftSystem();
    //     shiftSystem.setWaiter(waiter);
    //     shiftSystem.setDayOfWeek(dayOfWeek);
    //     shiftSystem.setDate(shiftSystemRequestDTO.getDate());
    //     shiftSystem.setStartTime(shiftSystemRequestDTO.getStartTime());
    //     shiftSystem.setEndTime(shiftSystemRequestDTO.getEndTime());
    //     return shiftSystem;
    // }
    
    public Optional<ShiftSystem> getShiftById(Long id) {
        return shiftSystemRepository.findById(id);
    }

    public List<ShiftSystem> getAllShifts() {
        return shiftSystemRepository.findAll();
    }
    
    public List<ShiftSystem> getShiftsByWaiterId(Long waiterId) {
        return shiftSystemRepository.findByWaiterId(waiterId);
    }

    public ShiftSystem updateShift(Long id, ShiftUpdateDTO shiftUpdateDTO) {
        ShiftSystem existingShift = shiftSystemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shift not found"));
    
        User waiter = userRepository.findById(shiftUpdateDTO.getWaiterId())
            .orElseThrow(() -> new RuntimeException("Waiter not found"));
    
        existingShift.setWaiter(waiter);
        existingShift.setDate(shiftUpdateDTO.getDate());
        existingShift.setStartTime(shiftUpdateDTO.getStartTime());
        existingShift.setEndTime(shiftUpdateDTO.getEndTime());
    
        return shiftSystemRepository.save(existingShift);
    }


    public void deleteShift(Long id) {
        shiftSystemRepository.deleteById(id);
    }

    
}