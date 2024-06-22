package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.ShiftSystem;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.payload.request.ShiftSystemRequestDTO;
import com.HelloWay.HelloWay.payload.request.ShiftTimeDTO;
import com.HelloWay.HelloWay.repos.ShiftSystemRepository;
import com.HelloWay.HelloWay.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ShiftSystemService {

    private final ShiftSystemRepository shiftSystemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShiftSystemService(ShiftSystemRepository shiftSystemRepository, UserRepository userRepository) {
        this.shiftSystemRepository = shiftSystemRepository;
        this.userRepository = userRepository;
    }

    public List<ShiftSystem> createShiftSystem(ShiftSystemRequestDTO shiftSystemRequest) {
        List<ShiftSystem> shiftSystems = new ArrayList<>();
        for (ShiftTimeDTO shiftTimeDTO : shiftSystemRequest.getShifts()) {
            Optional<User> userOptional = userRepository.findById(shiftTimeDTO.getWaiterId());

            if (userOptional.isPresent()) {
                User waiter = userOptional.get();

                LocalTime startTime = shiftTimeDTO.getStartTime();
                LocalTime endTime = shiftTimeDTO.getEndTime();
                LocalDate shiftDate = shiftTimeDTO.getDate();

                ShiftSystem shiftSystem = new ShiftSystem();
                shiftSystem.setWaiter(waiter);
                shiftSystem.setStartTime(startTime);
                shiftSystem.setEndTime(endTime);
                shiftSystem.setDate(shiftDate);
                shiftSystem.setType(shiftTimeDTO.getType()); // Set type to "shift"

                shiftSystems.add(shiftSystem);
            }
        }

        return shiftSystemRepository.saveAll(shiftSystems);
    }

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
            Optional<ShiftSystem> existingShiftOpt = shiftSystemRepository.findByDateAndWaiterId(shiftTimeDTO.getDate(), shiftTimeDTO.getWaiterId());

            User waiter = userRepository.findById(shiftTimeDTO.getWaiterId())
                    .orElseThrow(() -> new RuntimeException("Waiter not found with ID: " + shiftTimeDTO.getWaiterId()));

            if (existingShiftOpt.isPresent()) {
                ShiftSystem existingShift = existingShiftOpt.get();
                existingShift.setWaiter(waiter);
                existingShift.setDate(shiftTimeDTO.getDate());
                existingShift.setStartTime(shiftTimeDTO.getStartTime());
                existingShift.setEndTime(shiftTimeDTO.getEndTime());
                existingShift.setType(shiftTimeDTO.getType()); // Set type to "shift"
                updatedShifts.add(existingShift);
            } else {
                LocalTime startTime = shiftTimeDTO.getStartTime();
                LocalTime endTime = shiftTimeDTO.getEndTime();
                LocalDate shiftDate = shiftTimeDTO.getDate();

                ShiftSystem shiftSystem = new ShiftSystem();
                shiftSystem.setWaiter(waiter);
                shiftSystem.setStartTime(startTime);
                shiftSystem.setEndTime(endTime);
                shiftSystem.setDate(shiftDate);
                shiftSystem.setType(shiftTimeDTO.getType()); // Set type to "shift"
                updatedShifts.add(shiftSystem);
            }
        }

        return shiftSystemRepository.saveAll(updatedShifts);
    }

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
        return shifts.stream()
                .filter(shift -> !shift.getDate().isBefore(startDate) && "dayOff".equals(shift.getType()))
                .map(shift -> shift.getDate().getDayOfWeek())
                .findFirst();
    }
    
    @Transactional
    public int updateDayOff(Long waiterId, DayOfWeek newDayOff, LocalDate startDate, int durationInWeeks) {
        Optional<DayOfWeek> currentDayOffOpt = getCurrentDayOff(waiterId, startDate);
    
        if (currentDayOffOpt.isPresent()) {
            DayOfWeek currentDayOff = currentDayOffOpt.get();
            LocalDate endDate = startDate.plusWeeks(durationInWeeks);
    
            List<ShiftSystem> shifts = shiftSystemRepository.findByWaiterId(waiterId);
            if (shifts.isEmpty()) {
                throw new RuntimeException("No shifts found for waiter with ID: " + waiterId);
            }
    
            ShiftSystem lastShift = shifts.get(shifts.size() - 1);
    
            LocalTime startTime = lastShift.getStartTime();
            LocalTime endTime = lastShift.getEndTime();
    
            User waiter = userRepository.findById(waiterId)
                    .orElseThrow(() -> new RuntimeException("Waiter not found with ID: " + waiterId));
    
            int updatedShiftsCount = 0;
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                Optional<ShiftSystem> existingShiftOptional = shiftSystemRepository.findByDateAndWaiterId(date, waiterId);
    
                if (existingShiftOptional.isPresent()) {
                    ShiftSystem existingShift = existingShiftOptional.get();
                    if (dayOfWeek.equals(newDayOff)) {
                        existingShift.setType("dayOff"); // Set type to "dayOff"
                        updatedShiftsCount++;
                    } else if (dayOfWeek.equals(currentDayOff)) {
                        existingShift.setType("shift"); // Set type to "shift"
                        updatedShiftsCount++;
                    }
                    shiftSystemRepository.save(existingShift); // Save the updated shift
                } else {
                    System.out.println("No existing shift found to update for date: " + date);
                }
                // updatedShiftsCount++;
            }
    
            return updatedShiftsCount;
        }
    
        return 0;
    }    

}
