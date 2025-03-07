package com.HelloWay.HelloWay.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventDTO {
    private Long idEvent;
    private String eventTitle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private Integer nbParticipant;
    private Double price;
    private Boolean allInclusive;
    private Long idSpace;
}