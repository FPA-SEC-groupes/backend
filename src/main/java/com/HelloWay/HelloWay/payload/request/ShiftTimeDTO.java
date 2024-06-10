package com.HelloWay.HelloWay.payload.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class ShiftTimeDTO {
    private Long waiterId;
    private String dayOfWeek;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
