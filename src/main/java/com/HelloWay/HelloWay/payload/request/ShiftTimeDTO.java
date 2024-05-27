package com.HelloWay.HelloWay.payload.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ShiftTimeDTO {
    private String dayOfWeek;
    private String date;
    private String startTime;
    private String endTime;
    public LocalDate getShiftDate() {
        return LocalDate.parse(date);
    }
}
