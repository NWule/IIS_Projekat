package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.dto.MatchEventRequestDTO;
import com.football_club.MatchTracking.service.impl.LiveMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class LiveMatchController {
    private final LiveMatchService liveMatchService;

    @PostMapping("/game/{gameId}/event")
    public ResponseEntity<String> recordLiveEvent(@PathVariable Long gameId, @RequestBody MatchEventRequestDTO dto){
        try {
            liveMatchService.processLiveEvent(gameId, dto);
            return ResponseEntity.ok("Event successfully recorded. Statistics updated in real-time!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Greška na backendu: " + e.getMessage() + (e.getCause() != null ? " | Uzrok: " + e.getCause().getMessage() : ""));
        }
    }

    @PutMapping("/game/{gameId}/end")
    public ResponseEntity<String> endMatch(@PathVariable Long gameId) {
        try {
            liveMatchService.endLiveMatch(gameId);
            return ResponseEntity.ok("Utakmica je uspešno završena i upisana u bazu.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Greška pri završetku utakmice: " + e.getMessage());
        }
    }
}
