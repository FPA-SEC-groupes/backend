package com.HelloWay.HelloWay.payload.request;

import lombok.Data;

@Data
public class UpdateDayOffRequest {
    private Long waiterId;
    private String newDayOff;
    private String startDate;
    private int durationInWeeks;
}
