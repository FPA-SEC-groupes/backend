package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Space;
import com.HelloWay.HelloWay.entities.Wifi;
import com.HelloWay.HelloWay.exception.DuplicateEntityException;
import com.HelloWay.HelloWay.payload.request.WifiDTO;
import com.HelloWay.HelloWay.repos.SpaceRepository;
import com.HelloWay.HelloWay.repos.WifiRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

@Service
public class WifiService {
    @Autowired
    private WifiRepository wifiRepository;

     @Autowired
    private SpaceRepository spaceRepository;
    
    public List<WifiDTO> saveWifis(WifiDTO wifiDTO) {
        // 1. Input Validation
        if (wifiDTO.getWifis() == null || wifiDTO.getWifis().isEmpty()) {
            throw new IllegalArgumentException("List of WiFi information cannot be null or empty");
        }
    
        // 2. Retrieve Associated Space
        Space space = spaceRepository.findById(wifiDTO.getSpaceId())
                .orElseThrow(() -> new EntityNotFoundException("Space not found with id: " + wifiDTO.getSpaceId()));
    
        List<WifiDTO> savedWifiDTOs = new ArrayList<>();
        for (WifiDTO.WifiInfo wifiInfo : wifiDTO.getWifis()) { // Use WifiDTO.WifiInfo
            // 3. (Optional) Check for Duplicate WiFi (within this space)
            // if (wifiRepository.existsBySsidAndSpace(wifiInfo.getSsid(), space)) {
            //     throw new DuplicateEntityException("WiFi with SSID '" + wifiInfo.getSsid() + "' already exists for this space.");
            // }
    
            // 4. Convert to Wifi Entity (with Password Hashing)
            Wifi wifi = new Wifi();
            wifi.setSsid(wifiInfo.getSsid());
            wifi.setPassword(wifiInfo.getPassword()); 
            wifi.setSpace(space);
    
            // 5. Save Wifi
            Wifi savedWifi = wifiRepository.save(wifi);
    
            // 6. Convert to WifiDTO (without Password)
            WifiDTO savedWifiDTO = new WifiDTO();
            savedWifiDTO.setSpaceId(savedWifi.getSpace().getId_space()); // Set the spaceId for each WiFi
    
            // 7. Create a WifiInfo object and set SSID
            WifiDTO.WifiInfo savedWifiInfo = new WifiDTO.WifiInfo(); // Use WifiDTO.WifiInfo
            savedWifiInfo.setSsid(savedWifi.getSsid());
            List<WifiDTO.WifiInfo> wifis = new ArrayList<>(); // Create an empty list
            wifis.add(savedWifiInfo);
            savedWifiDTO.setWifis(wifis); // Set the updated wifis list
    
            savedWifiDTOs.add(savedWifiDTO); 
        }
    
        return savedWifiDTOs;
    }
    
    public Optional<Wifi> getWifiById(Long id) {
        return wifiRepository.findById(id);
    }

    public List<Wifi> getAllWifis() {
        return wifiRepository.findAll();
    }

    public List<Wifi> getWifisBySpaceId(Long spaceId) {
        return wifiRepository.findWifisBySpaceId(spaceId);
    }

    public Wifi updateWifi(Wifi wifi) {
        return wifiRepository.save(wifi);
    }

    public void deleteWifi(Long id) {
        wifiRepository.deleteById(id);
    }
}
