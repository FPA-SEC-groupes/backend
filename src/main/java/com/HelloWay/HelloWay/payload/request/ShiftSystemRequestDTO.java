package com.HelloWay.HelloWay.payload.request;

import java.util.List;

import lombok.Data;

@Data
public class ShiftSystemRequestDTO {
    private Long waiterId;
    private List<ShiftTimeDTO> shiftTimes;
}
