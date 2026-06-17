package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.model.graph.TacticalAnalysisGraph;
import com.football_club.MatchTracking.service.ITacticalAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tactical-analysis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TacticalAnalysisController {

    private final ITacticalAnalysisService analysisService;

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<TacticalAnalysisGraph>> getAnalysisByGame(@PathVariable Long gameId) {
        List<TacticalAnalysisGraph> analysisList = analysisService.getAnalysisForGame(gameId);
        return ResponseEntity.ok(analysisList);
    }
}