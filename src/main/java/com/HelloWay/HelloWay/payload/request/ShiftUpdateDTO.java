package com.HelloWay.HelloWay.payload.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.HelloWay.HelloWay.entities.DayOfWeek;

import lombok.Data;

@Data
public class ShiftUpdateDTO {
    private Long waiterId;
    private Long shiftId;
    private DayOfWeek dayOfWeek;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}
