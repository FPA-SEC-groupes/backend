package com.HelloWay.HelloWay.payload.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WifiDTO {
    private Long spaceId;
    private List<WifiInfo> wifis; 

    // Inner class for WifiInfo
    @Data
    public static class WifiInfo {
        private String ssid;
        private String password;
    }
}
