package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.config.QRCodeGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QRCodeController {
    @GetMapping(value = "/api/qrcodes/{tableIdentifier}/zoneId/{zoneId}", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Long tableIdentifier, @PathVariable Long zoneId) {
        try {
            byte[] qrCodeImage = QRCodeGenerator.generateQRCodeImage(tableIdentifier, zoneId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
