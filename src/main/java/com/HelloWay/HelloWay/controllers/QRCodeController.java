package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.Security.QrTokens;
import com.HelloWay.HelloWay.config.QRCodeGenerator;
import com.HelloWay.HelloWay.entities.Board;
import com.HelloWay.HelloWay.repos.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BoardRepository boardRepository;

    // L'appartenance de la zone au gérant est vérifiée par OwnershipInterceptor (zoneId)
    @GetMapping(value = "/api/qrcodes/{tableIdentifier}/zoneId/{zoneId}", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Long tableIdentifier, @PathVariable Long zoneId) {
        Board board = boardRepository.findById(tableIdentifier).orElse(null);
        if (board == null || board.getZone() == null || !zoneId.equals(board.getZone().getIdZone())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            if (board.getQrToken() == null) {
                board.setQrToken(QrTokens.newToken());
                boardRepository.save(board);
            }
            byte[] qrCodeImage = QRCodeGenerator.generateQRCodeImage(tableIdentifier, board.getQrToken(), zoneId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
