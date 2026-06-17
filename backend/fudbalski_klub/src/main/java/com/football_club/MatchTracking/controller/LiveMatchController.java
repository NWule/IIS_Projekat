package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.dto.MatchEventRequestDTO;
import com.football_club.MatchTracking.service.impl.LiveMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class LiveMatchController {
    private final LiveMatchService liveMatchService;

    @PostMapping("/game/{gameId}/event")
    public ResponseEntity<String> recordLiveEvent(@PathVariable Long gameId, @RequestBody MatchEventRequestDTO dto){
        liveMatchService.processLiveEvent(gameId, dto);

        return ResponseEntity.ok("Event successfully recorded. Statistics updated in real-time!");
    }
}
