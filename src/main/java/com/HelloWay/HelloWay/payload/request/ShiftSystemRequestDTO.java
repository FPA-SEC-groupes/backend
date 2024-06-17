package com.HelloWay.HelloWay.payload.request;

import java.util.List;
import lombok.Data;

@Data
public class ShiftSystemRequestDTO {
    private List<ShiftTimeDTO> shifts; // Correctly use ShiftTimeDTO here
}
