package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final IReportService reportService;

    @GetMapping("/game/{gameId}/pdf")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'STATISTICIAN', 'ADMIN')")
    public ResponseEntity<byte[]> downloadGameReportPdf(@PathVariable Long gameId) {

        byte[] pdfBytes = reportService.generateMatchReportPdf(gameId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Game_Report_" + gameId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
