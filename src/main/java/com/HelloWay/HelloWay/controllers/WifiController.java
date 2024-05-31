package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.Wifi;
import com.HelloWay.HelloWay.payload.request.WifiDTO;
import com.HelloWay.HelloWay.services.WifiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/wifis")
public class WifiController {
    @Autowired
    private WifiService wifiService;

   @PostMapping
    public List<WifiDTO> addWifi(@Valid @RequestBody WifiDTO wifiDTO) { // Changed parameter type to WifiDTO
        return wifiService.saveWifis(wifiDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wifi> getWifiById(@PathVariable Long id) {
        return wifiService.getWifiById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    

    @GetMapping("/")
    public List<Wifi> getAllWifis() {
        return wifiService.getAllWifis();
    }

    @PutMapping("/")
    public Wifi updateWifi(@RequestBody Wifi wifi) {
        return wifiService.updateWifi(wifi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWifi(@PathVariable Long id) {
        wifiService.deleteWifi(id);
        return ResponseEntity.ok().build();
    }
}
