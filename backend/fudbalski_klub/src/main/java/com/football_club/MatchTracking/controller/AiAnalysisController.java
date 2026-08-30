package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.service.IAiTacticalAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-analysis")
@RequiredArgsConstructor
public class AiAnalysisController {
    private final IAiTacticalAnalysisService aiTacticalAnalysisService;

    @PostMapping("/game/{gameId}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ADMIN')")
    public ResponseEntity<String> generateAiReport(@PathVariable Long gameId) {
        try {
            String report = aiTacticalAnalysisService.generateMatchReport(gameId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Greška: " + e.getMessage());
        }
    }
}
