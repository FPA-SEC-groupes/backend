package com.HelloWay.HelloWay.payload.request;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class ShiftRequest {

        private Long waiterId;
        private String date;
        private String start;
        private String end;
        public LocalDate getShiftDate() {
        return LocalDate.parse(date);
    }

    public LocalTime getStartTime() {
        return LocalTime.parse(start);
    }

    public LocalTime getEndTime() {
        return LocalTime.parse(end);
    }
}
