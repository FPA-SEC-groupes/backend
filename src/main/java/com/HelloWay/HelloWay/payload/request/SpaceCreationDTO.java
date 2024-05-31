package com.HelloWay.HelloWay.payload.request;

import lombok.Data;
import java.util.List;

@Data
public class SpaceCreationDTO {
    private String titleSpace;
    private String description;
    private String latitude;
    private String longitude;
    private String validation;
    private Long phoneNumber;
    private Long numberOfRate;
    private double surfaceEnM2;
    private List<WifiInfo> wifis;

    @Data
    public static class WifiInfo {
        private String ssid;
        private String password;
    }
     
}
