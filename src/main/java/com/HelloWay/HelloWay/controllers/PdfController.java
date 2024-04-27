package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.Space;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.services.PdfGenerationService;
import com.HelloWay.HelloWay.services.SpaceService;
import com.itextpdf.text.DocumentException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PdfController {
    private final PdfGenerationService pdfGenerationService;
    private final SpaceService spaceService;

    public PdfController(PdfGenerationService pdfGenerationService, SpaceService spaceService) {
        this.pdfGenerationService = pdfGenerationService;
        this.spaceService = spaceService;
    }

    @GetMapping("/generate-pdf/space/{spaceId}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<InputStreamResource> generatePdf(@PathVariable long spaceId) throws DocumentException {
        Space space = spaceService.findSpaceById(spaceId);
        if (space == null){
            return ResponseEntity.notFound().build();
        }
        List<User> servers = space.getServers(); // Implement this method to fetch data from your database or source
        ByteArrayInputStream pdfStream = pdfGenerationService.generatePdf(servers);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=servers_data.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}
