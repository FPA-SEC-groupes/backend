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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // @Transactional
    // public ShiftSystem createShiftSystem(ShiftSystemRequestDTO shiftSystemRequestDTO) {
    //     Optional<User> user = userRepository.findById(shiftSystemRequestDTO.getWaiterId());
    //     if (!user.isPresent()) {
    //         throw new RuntimeException("User not found");
    //     }
        
    //     DayOfWeek dayOfWeek = DayOfWeek.valueOf(shiftSystemRequestDTO.getDayOfWeek().toUpperCase());

    //     User waiter = user.get();
    //     ShiftSystem shiftSystem = new ShiftSystem();
    //     shiftSystem.setWaiter(waiter);
    //     shiftSystem.setDayOfWeek(dayOfWeek);
    //     shiftSystem.setDate(shiftSystemRequestDTO.getDate());
    //     shiftSystem.setStartTime(shiftSystemRequestDTO.getStartTime());
    //     shiftSystem.setEndTime(shiftSystemRequestDTO.getEndTime());

    //     return shiftSystemRepository.save(shiftSystem); // Save the shiftSystem to the database
    // }
    



    public List<ShiftSystem> createShiftSystem(ShiftSystemRequestDTO shiftSystemRequest) {
        List<ShiftSystem> shiftSystems = new ArrayList<>();
        for (ShiftTimeDTO shiftTimeDTO : shiftSystemRequest.getShifts()) {
            Optional<User> userOptional = userRepository.findById(shiftTimeDTO.getWaiterId());

            if (userOptional.isPresent()) {
                User waiter = userOptional.get();

                DayOfWeek dayOfWeek = DayOfWeek.valueOf(shiftTimeDTO.getDayOfWeek().toUpperCase());
                LocalTime startTime = shiftTimeDTO.getStartTime();
                LocalTime endTime = shiftTimeDTO.getEndTime();
                LocalDate shiftDate = shiftTimeDTO.getDate();

                ShiftSystem shiftSystem = new ShiftSystem();
                shiftSystem.setWaiter(waiter);
                shiftSystem.setDayOfWeek(dayOfWeek);
                shiftSystem.setStartTime(startTime);
                shiftSystem.setEndTime(endTime);
                shiftSystem.setDate(shiftDate);

                shiftSystems.add(shiftSystem);
            }
        }

        return shiftSystemRepository.saveAll(shiftSystems);
    }
    
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

    public List<ShiftSystem> updateShiftsByDate(List<ShiftTimeDTO> shiftUpdateDTOs) {
        List<ShiftSystem> updatedShifts = new ArrayList<>();

            for (ShiftTimeDTO shiftTimeDTO : shiftUpdateDTOs) {
                Optional<ShiftSystem> existingShiftOpt = shiftSystemRepository.findByDate(shiftTimeDTO.getDate());

                User waiter = userRepository.findById(shiftTimeDTO.getWaiterId())
                        .orElseThrow(() -> new RuntimeException("Waiter not found with ID: " + shiftTimeDTO.getWaiterId()));

                if (existingShiftOpt.isPresent()) {
                    ShiftSystem existingShift = existingShiftOpt.get();
                    existingShift.setWaiter(waiter);
                    existingShift.setDate(shiftTimeDTO.getDate());
                    existingShift.setStartTime(shiftTimeDTO.getStartTime());
                    existingShift.setEndTime(shiftTimeDTO.getEndTime());
                    updatedShifts.add(existingShift);
                } else {
                    DayOfWeek dayOfWeek = DayOfWeek.valueOf(shiftTimeDTO.getDayOfWeek().toUpperCase());
                    LocalTime startTime = shiftTimeDTO.getStartTime();
                    LocalTime endTime = shiftTimeDTO.getEndTime();
                    LocalDate shiftDate = shiftTimeDTO.getDate();

                    ShiftSystem shiftSystem = new ShiftSystem();
                    shiftSystem.setWaiter(waiter);
                    shiftSystem.setDayOfWeek(dayOfWeek);
                    shiftSystem.setStartTime(startTime);
                    shiftSystem.setEndTime(endTime);
                    shiftSystem.setDate(shiftDate);
                    updatedShifts.add(shiftSystem);
                }
            }

        return shiftSystemRepository.saveAll(updatedShifts);
    } 
    // public ShiftSystem updateShift(Long id, ShiftUpdateDTO shiftUpdateDTO) {
    //     ShiftSystem existingShift = shiftSystemRepository.findById(id)
    //         .orElseThrow(() -> new RuntimeException("Shift not found"));
    
    //     User waiter = userRepository.findById(shiftUpdateDTO.getWaiterId())
    //         .orElseThrow(() -> new RuntimeException("Waiter not found"));
    
    //     existingShift.setWaiter(waiter);
    //     existingShift.setDate(shiftUpdateDTO.getDate());
    //     existingShift.setStartTime(shiftUpdateDTO.getStartTime());
    //     existingShift.setEndTime(shiftUpdateDTO.getEndTime());
    
    //     return shiftSystemRepository.save(existingShift);
    // }


    public void deleteShift(Long id) {
        shiftSystemRepository.deleteById(id);
    }
    private static final List<DayOfWeek> allDays = Stream.of(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
    ).collect(Collectors.toList());

    public Optional<DayOfWeek> getCurrentDayOff(Long waiterId, LocalDate startDate) {
        List<ShiftSystem> shifts = shiftSystemRepository.findByWaiterId(waiterId);
        Set<DayOfWeek> daysOfWeek = shifts.stream()
                .filter(shift -> !shift.getDate().isBefore(startDate))
                .map(ShiftSystem::getDayOfWeek)
                .collect(Collectors.toSet());

        for (DayOfWeek day : allDays) {
            if (!daysOfWeek.contains(day)) {
                return Optional.of(day);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public boolean updateDayOff(Long waiterId, DayOfWeek newDayOff, LocalDate startDate, int durationInWeeks) {
        Optional<DayOfWeek> currentDayOffOpt = getCurrentDayOff(waiterId, startDate);

        if (currentDayOffOpt.isPresent()) {
            DayOfWeek currentDayOff = currentDayOffOpt.get();
            LocalDate endDate = startDate.plusWeeks(durationInWeeks);

            List<ShiftSystem> shifts = shiftSystemRepository.findByWaiterId(waiterId);
            ShiftSystem lastShift = shifts.get(shifts.size() - 1);

            // Extract start and end times
            LocalTime startTime = lastShift.getStartTime();
            LocalTime endTime = lastShift.getEndTime();

            User waiter = userRepository.findById(waiterId)
                    .orElseThrow(() -> new RuntimeException("Waiter not found with ID: " + waiterId));

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                if (dayOfWeek.equals(newDayOff)) {
                    shiftSystemRepository.deleteByWaiterIdAndDate(waiterId, date);
                } else if (dayOfWeek.equals(currentDayOff)) {
                    ShiftSystem newShift = new ShiftSystem();
                    newShift.setWaiter(waiter);
                    newShift.setDayOfWeek(dayOfWeek);
                    newShift.setDate(date);
                    newShift.setStartTime(startTime);
                    newShift.setEndTime(endTime);
                    shiftSystemRepository.save(newShift);
                }
            }

            return true;
        }

        return false;
    }
    
}