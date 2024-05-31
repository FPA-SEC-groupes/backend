package com.HelloWay.HelloWay.payload.request;

import lombok.Data;

@Data // Inner class to hold SSID and password for each WiFi
class WifiInfo {
    private String ssid;
    private String password;
}
