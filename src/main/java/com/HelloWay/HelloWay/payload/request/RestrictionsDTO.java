package com.HelloWay.HelloWay.payload.request;


import lombok.Data;

@Data
public class RestrictionsDTO {
    private Long id;
    private String description;
    private Long userId;
    private Long reservationId;
}
